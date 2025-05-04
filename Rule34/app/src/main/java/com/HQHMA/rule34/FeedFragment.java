package com.HQHMA.rule34;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.HQHMA.rule34.Adapters.RecyclerViewAdapterFeedItems;
import com.HQHMA.rule34.Adapters.RecyclerViewAdapterSearchSuggests;
import com.HQHMA.rule34.Adapters.RecyclerViewAdapterTags;
import com.HQHMA.rule34.Models.Posts;
import com.HQHMA.rule34.Models.Tag;
import com.HQHMA.rule34.Utilities.JSONUtilities;
import com.HQHMA.rule34.Utilities.UrlUtilities;
import com.HQHMA.rule34.Utilities.Utilities;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class FeedFragment extends Fragment {

    ArrayList<Posts> posts = new ArrayList<>();
    ArrayList<Tag> tags = new ArrayList<>();
    ArrayList<Tag> search_tags;
    private RecyclerView recyclerView, recyclerViewTags,searchViewSuggestList;
    private ConstraintLayout searchViewSuggestListParent;
    private ConstraintLayout tagsLayoutParent;
    private ImageButton saveTagCollectionBtn;
    private RecyclerView.Adapter mAdapter , mAdapterTags,mAdapterSuggests;
    private RecyclerView.LayoutManager layoutManager;
    public SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar,suggestListProgressBar;
    private ImageButton settingBtn;
    TextView errorTV;
    RequestQueue requestQueueForTags;

    Handler handlerForDelaySearchView = new Handler(Looper.getMainLooper());
    String currentTextForDelaySearchView = "";
    Runnable searchRunnableForDelaySearchView;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance(String param1) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        if (!param1.isEmpty()) {
            args.putString("tags", param1);
            fragment.setArguments(args);
        }
        return fragment;
    }

    public String getSearchText(boolean getColorAsWell) {
        String allTags = null;
        for (Tag tag:tags) {
            String temp= tag.getValue();
            if (getColorAsWell)
                temp +=":" + tag.getTypeIndex();

            if (allTags == null)
                allTags = temp;
            else
                allTags += " " + temp;
        }
        //searchView.getQuery().toString().trim().replaceAll(" +", " ");
        return allTags;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestQueueForTags = Volley.newRequestQueue(getContext());
        if (getArguments() != null) {
            String[] inputTags = getArguments().getString("tags").split(" ");
            for (String tag:inputTags) {
                String[] temp = tag.split(":");
                tags.add(new Tag(temp[0],0L,0L,Integer.parseInt(temp[1])));
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        saveTagCollectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNameInputDialog();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()){
                    hideSearchViewSuggests();

                    Utilities.hideKeyboard(view,getActivity());
                    searchView.clearFocus();

                    searchView.setQuery("",true);
                    tags.add(new Tag(query));
                    mAdapterTags.notifyDataSetChanged();
                    divider_state();

                    String url = UrlUtilities.getUrl(getSearchText(false),getContext());
                    reupdate_theJSON(url);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                 if (newText.contains(" ")) {
                     hideSearchViewSuggests();
                    newText = newText.replaceAll(" ", "");
                    if (!newText.equals("")){
                        searchView.setQuery("",true);
                        tags.add(new Tag(newText , 0L));
                        mAdapterTags.notifyDataSetChanged();
                        divider_state();

                        String url = UrlUtilities.getUrl(getSearchText(false),getContext());
                        reupdate_theJSON(url);
                    }else {
                        searchView.setQuery("",true);
                    }
                }else { // if text not submitted yet
                     // Remove any previous callbacks to the handler
                     handlerForDelaySearchView.removeCallbacks(searchRunnableForDelaySearchView);

                     // Update the current text
                     currentTextForDelaySearchView = newText;

                     // Create a new Runnable
                     searchRunnableForDelaySearchView = new Runnable() {
                         @Override
                         public void run() {
                             // Check if the text has not changed
                             String liveSearchViewText = searchView.getQuery().toString();
                             if (currentTextForDelaySearchView.equals(liveSearchViewText)) {
                                 // Perform your action here
                                 doSearchForSuggests(liveSearchViewText);
                             }
                         }
                     };

                     // Post the Runnable with a 2-second delay
                     handlerForDelaySearchView.postDelayed(searchRunnableForDelaySearchView, 600);
                 }
                return true;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String url = UrlUtilities.getUrl(getSearchText(false),getContext());

                reupdate_theJSON(url);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        String startUrl = UrlUtilities.getUrl(getSearchText(false),getContext());
        reupdate_theJSON(startUrl);

    }

    private void doSearchForSuggests(String text){
        if (text.length() >= 3){
            requestQueueForTags.stop();
            requestQueueForTags.cancelAll(true);
            clearSearchViewSuggests();

            String tagsUrl = UrlUtilities.getTagsUrlForSearchSuggest(text);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, tagsUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    search_tags = new ArrayList<>();
                    JSONUtilities.convertXMLToTags(response,search_tags);
                    Log.i("TAG123", "SearchView Suggests: " + search_tags.toString());

                    fillSearchViewSuggests(search_tags);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideSearchViewSuggests();
                    Log.e("Error VolleyError", error.toString());
                }
            });
            requestQueueForTags.add(stringRequest);
            requestQueueForTags.start();
        }else {
            requestQueueForTags.stop();
            requestQueueForTags.cancelAll(true);
            hideSearchViewSuggests();
        }
    }

    private void hideSearchViewSuggests(){
        searchViewSuggestList.setAdapter(null);
        searchViewSuggestListParent.setVisibility(View.GONE);
        suggestListProgressBar.setVisibility(View.GONE);
    }

    private void clearSearchViewSuggests(){
        searchViewSuggestList.setAdapter(null);
        searchViewSuggestListParent.setVisibility(View.VISIBLE);
        suggestListProgressBar.setVisibility(View.VISIBLE);
    }

    private void fillSearchViewSuggests(ArrayList<Tag> tags){
        //searchViewSuggestList.setHasFixedSize(true);
        searchViewSuggestList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapterSuggests = new RecyclerViewAdapterSearchSuggests(tags,getContext(),this);
        searchViewSuggestList.setAdapter(mAdapterSuggests);

        searchViewSuggestListParent.setVisibility(View.VISIBLE);
        suggestListProgressBar.setVisibility(View.GONE);
    }

    public void reupdate_theJSON(String url) {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        errorTV.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                posts.clear();
                JSONUtilities.convertJSONToPosts(response,posts);
                mAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.scrollToPosition(0);
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error VolleyError", error.toString());
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                errorTV.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.smoothScrollToPosition(0);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        loadRecyclerView(view,posts);
        loadRecyclerViewTags(view,tags);

        searchViewSuggestList = view.findViewById(R.id.searchViewSuggestList);
        searchViewSuggestListParent = view.findViewById(R.id.searchViewSuggestListParent);
        suggestListProgressBar = view.findViewById(R.id.suggestListProgressBar);

        tagsLayoutParent = view.findViewById(R.id.tagsLayoutParent);
        saveTagCollectionBtn = view.findViewById(R.id.saveTagsCollection);
        searchView = view.findViewById(R.id.searchView);
        swipeRefreshLayout = view.findViewById(R.id.SwipeRefreshLayout);
        errorTV = view.findViewById(R.id.errorTV);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        settingBtn = view.findViewById(R.id.settingBtn);
        divider_state();
        return view;
    }


    private void loadRecyclerView(View view,ArrayList<Posts> posts) {
        recyclerView = view.findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerViewAdapterFeedItems(posts,getContext(),getActivity(),this);
        recyclerView.setAdapter(mAdapter);
    }
    private void loadRecyclerViewTags(View view, ArrayList<Tag> tags) {
        recyclerViewTags = view.findViewById(R.id.recyclerViewTags);
        //recyclerViewTags.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerViewTags.setLayoutManager(layoutManager);
        mAdapterTags = new RecyclerViewAdapterTags(tags,getContext(),this);
        recyclerViewTags.setAdapter(mAdapterTags);

    }

    public void divider_state(){
        if (tags.isEmpty()){
            tagsLayoutParent.setVisibility(View.GONE);

        }else {
            tagsLayoutParent.setVisibility(View.VISIBLE);
        }
    }

    private void showNameInputDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.save_collection_dialog, null);

        EditText input = dialogView.findViewById(R.id.editText);
        input.requestFocus();

        TextView saveBTN = dialogView.findViewById(R.id.saveBTN);
        TextView cancelBTN = dialogView.findViewById(R.id.notNowBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Ensure the keyboard pops up when the dialog is displayed
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();


        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = input.getText().toString().trim();
                if (!name.isEmpty()) {
                    // Do something with the name (e.g., save it)

                    saveTagList(name);

                    Toast.makeText(getActivity(), "saved: " + name, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    private void saveTagList(String name){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Rule34",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String lastSavedTags = sharedPreferences.getString("collectionTags","");
        String newTagListForSave = name + ";" + getSearchText(true);

        String finalForSave;
        if (lastSavedTags.isEmpty())
            finalForSave = newTagListForSave;
        else
            finalForSave = lastSavedTags + "\n" + newTagListForSave;

        Log.i("TAG123", "saveTagList: " + finalForSave);
        editor.putString("collectionTags",finalForSave);
        editor.apply();
    }

    public void setSearchView(Tag thisTag){
        hideSearchViewSuggests();

        searchView.setQuery("",true);
        tags.add(tags.size(),thisTag);
        mAdapterTags.notifyItemInserted(tags.size()-1);
        recyclerViewTags.smoothScrollToPosition(tags.size()-1);
        divider_state();

        String url = UrlUtilities.getUrl(getSearchText(false),getContext());
        reupdate_theJSON(url);
    }
}