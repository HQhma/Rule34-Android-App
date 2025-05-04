package com.HQHMA.rule34.Adapters;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.HQHMA.rule34.FeedFragment;
import com.HQHMA.rule34.MainActivity;
import com.HQHMA.rule34.Models.Posts;
import com.HQHMA.rule34.Models.Tag;
import com.HQHMA.rule34.R;
import com.HQHMA.rule34.Utilities.FileManager;
import com.HQHMA.rule34.Utilities.JSONUtilities;
import com.HQHMA.rule34.Utilities.UrlUtilities;
import com.HQHMA.rule34.Utilities.Utilities;
import com.HQHMA.rule34.VideoPlayerActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;

public class RecyclerViewAdapterFeedItems extends RecyclerView.Adapter<RecyclerViewAdapterFeedItems.MyViewHolder> {

    private static final int VIEW_TYPE_ITEMS = R.layout.item_row;
    private static final int VIEW_TYPE_END_ITEM = R.layout.item_row_end;

    ArrayList<Posts> posts = new ArrayList<>();
    Context context;
    Activity activity;
    FeedFragment feedFragment;
    SharedPreferences sharedPreferences;
    int screenHeight;

    public RecyclerViewAdapterFeedItems(ArrayList<Posts> posts, Context context, Activity activity,FeedFragment feedFragment) {
        this.posts = posts;
        this.context = context;
        this.activity = activity;
        this.feedFragment = feedFragment;

        screenHeight = Utilities.getDisplayHeight(activity);

        sharedPreferences = context.getSharedPreferences("Rule34",MODE_PRIVATE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return posts.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == posts.size()) ? VIEW_TYPE_END_ITEM : VIEW_TYPE_ITEMS;
        //return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (position == posts.size()){
            return;
        }

        Posts thisPost = posts.get(position);

        holder.usernameTV.setText(thisPost.getOwner());
        holder.imageSizeMBTV.setVisibility(GONE);

        String[] tagTexts = thisPost.getTagsStr().split(" ");
        holder.tags = new ArrayList<Tag>();
        for (String tagText:tagTexts) {
            holder.tags.add(new Tag(tagText));
        }

        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileManager.downloadFile(thisPost.getFile_url(),thisPost.getFile_name(),activity);
            }
        });

        holder.reloadBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.progressBar.setVisibility(VISIBLE);
                holder.reloadBTN.setVisibility(GONE);
                loadFileInThePost(holder,position);
            }
        });

        holder.expandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressingForLoadTags(holder,true);

                loadRecyclerViewTags(thisPost,holder,holder.tags);

                feedFragment.searchView.clearFocus();
                Utilities.hideKeyboard(view,activity);
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!thisPost.getHighQualityLoaded()){
                    thisPost.setHighQualityLoaded(true);
                    loadImages(holder.imageView,position,thisPost.getFile_url(),holder.progressBar,holder.reloadBTN);
                    holder.imageSizeMBTV.setVisibility(GONE);
                }
            }
        });

        setImageViewHeightAndWidth(holder.imageView,thisPost.getHeight(),thisPost.getWidth());

        progressingForLoadTags(holder,false);
        if (holder.tags.size() <= 0)
            holder.expandBtn.setVisibility(GONE);

        holder.imageView.setImageResource(0);
        holder.imageSizeMBTV.setVisibility(GONE);
        loadFileInThePost(holder,position);
    }

    private void loadFileInThePost(MyViewHolder holder,int position){
        Posts thisPost = posts.get(position);
        holder.progressBar.setVisibility(VISIBLE);
        holder.reloadBTN.setVisibility(GONE);

        if (thisPost.getFile_url().contains(".mp4")){ //if it was mp4
            holder.playBTN.setVisibility(VISIBLE);
            holder.playBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("video_url",thisPost.getFile_url());
                    intent.putExtra("video_name",thisPost.getFile_name());
                    intent.putExtra("height",thisPost.getHeight());
                    intent.putExtra("width",thisPost.getWidth());
                    intent.putExtra("tags",thisPost.getTagsStr());
                    context.startActivity(intent);
                }
            });
            thisPost.setHighQualityLoaded(true);
            loadImages(holder.imageView,position,thisPost.getSample_url(),holder.progressBar,holder.reloadBTN);
            holder.imageSizeMBTV.setVisibility(GONE);

        }else { //if it was image
            holder.playBTN.setVisibility(GONE);

            if (thisPost.getHighQualityLoaded()){
                loadImages(holder.imageView,position, thisPost.getFile_url(), holder.progressBar, holder.reloadBTN);
            }else {
                if (sharedPreferences.getBoolean("highResCB_Bool", true)) {
                    Utilities.getImageSizeOnline(thisPost.getFile_url(), new Utilities.ImageSizeCallback() {
                        @Override
                        public void onSuccess(long sizeInBytes) {
                            int limit = sharedPreferences.getInt("downloadLimitSeekBarProgress", 4) + 1;
                            if (sizeInBytes / 1024.0 / 1024.0 < limit  || limit == 11) {
                                loadImages(holder.imageView,position, thisPost.getFile_url(), holder.progressBar, holder.reloadBTN);
                                thisPost.setHighQualityLoaded(true);
                                holder.imageSizeMBTV.setVisibility(GONE);
                            }else {
                                loadImages(holder.imageView,position, thisPost.getPreview_url(), holder.progressBar, holder.reloadBTN);
                                holder.imageSizeMBTV.setText(String.format("%.2f MB", sizeInBytes / 1024.0 / 1024.0));
                                holder.imageSizeMBTV.setVisibility(VISIBLE);
                                thisPost.setHighQualityLoaded(false);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            loadImages(holder.imageView,position, thisPost.getFile_url(), holder.progressBar, holder.reloadBTN);
                            holder.imageSizeMBTV.setVisibility(GONE);
                            thisPost.setHighQualityLoaded(true);
                        }
                    });
                }else {
                    loadImages(holder.imageView,position, thisPost.getPreview_url(), holder.progressBar, holder.reloadBTN);
                    thisPost.setHighQualityLoaded(false);
                }
            }
        }
    }

    private void loadRecyclerViewTags(Posts thisPost,MyViewHolder holder, ArrayList<Tag> tags) {
        progressingForLoadTags(holder,true);
        ReloadTagsAndSortThemAndCreateRecyclerViewAdapterAndSetIt(holder,tags);

    }

    private void progressingForLoadTags(MyViewHolder holder,boolean progressingOrNot){
        if (progressingOrNot){
            holder.tagsProgressBar.setVisibility(VISIBLE);
            holder.tagsProgressBar2.setVisibility(VISIBLE);
            holder.expandBtn.setVisibility(GONE);
        }else {
            holder.tagsProgressBar.setVisibility(GONE);
            holder.tagsProgressBar2.setVisibility(GONE);
            holder.expandBtn.setVisibility(VISIBLE);
        }
    }
    private void errorWhileLoadingTags(MyViewHolder holder){
        progressingForLoadTags(holder,false);
        Toast.makeText(context, "network error", Toast.LENGTH_SHORT).show();
    }

    int requestQueueLastItemIn;
    private void ReloadTagsAndSortThemAndCreateRecyclerViewAdapterAndSetIt(MyViewHolder holder, ArrayList<Tag> tags){

        SharedPreferences sharedPreferencesTags = context.getSharedPreferences("Rule34_Tags",MODE_PRIVATE);
        SharedPreferences.Editor editorTags = sharedPreferencesTags.edit();

        RequestQueue requestQueueForTags = Volley.newRequestQueue(context);
        requestQueueLastItemIn = -1;

        holder.tagsProgressBar2.setProgress(0);
        holder.tagsProgressBar2.setMax(tags.size());

        for (int i = 0; i < tags.size(); i++) {
            int thisTagColor = sharedPreferencesTags.getInt(tags.get(i).getValue(),-1);
            if (thisTagColor != -1){
                holder.tagsProgressBar2.incrementProgressBy(1);
                tags.get(i).setType(thisTagColor);
                Log.i("TAG123", "ReloadTagsAndSortThemAndCreateRecyclerViewAdapterAndSetIt: " + tags.get(i).getValue() + " exist before");
                if (i == tags.size() - 1 && requestQueueLastItemIn == -1){
                    tags.sort(Comparator.comparingInt(Tag::getTypeIndex));
                    progressingForLoadTags(holder,false);
                    setRecyclerViewTags(tags);
                }
            }else {
                int finalI = i;
                String value = tags.get(i).getValue();

                String tagsUrl = UrlUtilities.getTagUrl(value);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, tagsUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Tag tempTag = JSONUtilities.convertXMLToTag(response);
                        tags.get(finalI).setType(tempTag.getTypeIndex());

                        Log.i("TAG123", "SearchView Suggests: " + tempTag.toString());

                        editorTags.putInt(value,tags.get(finalI).getTypeIndex());
                        editorTags.apply();

                        holder.tagsProgressBar2.incrementProgressBy(1);

                        if (finalI == requestQueueLastItemIn){
                            tags.sort(Comparator.comparingInt(Tag::getTypeIndex));
                            progressingForLoadTags(holder,false);
                            setRecyclerViewTags(tags);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (finalI == requestQueueLastItemIn){
                            errorWhileLoadingTags(holder);
                        }
                        Log.e("Error VolleyError", error.toString());
                    }
                });

                requestQueueLastItemIn = i;
                requestQueueForTags.add(stringRequest);
            }
        }
        requestQueueForTags.start();
    }

    private void setRecyclerViewTags(ArrayList<Tag> tags) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showBottomDialog(tags);
    }

    private void setImageViewHeightAndWidth(ImageView imageView,int height,int width){
        //int displayHeight = Utilities.getDisplayHeight(activity);
        int displayWidth = Utilities.getDisplayWidth(activity);

        ViewGroup.LayoutParams params = imageView.getLayoutParams();

        float ratio = (float) width / displayWidth;
        int final_height = (int) (height / ratio);
        int finalwidth = displayWidth;

        if (final_height > 1500){
            float ratio2 = (float) final_height / 1500;
            finalwidth = (int) (finalwidth / ratio2);
            final_height = 1500;
        }

        params.height = final_height;
        params.width = finalwidth;

        imageView.setLayoutParams(params);
    }

    private void loadImages(ImageView imageView,int position, String url,ProgressBar progressBar,ImageButton reloadBTN) {
        Posts thisPost = posts.get(position);
        Log.i("loadImages", "loadImages: " + url);
        progressBar.setVisibility(VISIBLE);
        reloadBTN.setVisibility(GONE);
        Picasso
                .get()
                .load(url)
                .into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(GONE);
            }
            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(GONE);
                reloadBTN.setVisibility(VISIBLE);
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageButton /*saveBtn,*/downloadBtn,expandBtn,reloadBTN;
        ImageView imageView;

        TextView usernameTV,imageSizeMBTV;
        ProgressBar progressBar,tagsProgressBar,tagsProgressBar2;
        ConstraintLayout parent;

        View playBTN;

        ArrayList<Tag> tags = new ArrayList<Tag>();

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            /*saveBtn = itemView.findViewById(R.id.saveBtn);*/
            downloadBtn = itemView.findViewById(R.id.downloadBtn);
            expandBtn = itemView.findViewById(R.id.expandBtn);
            reloadBTN = itemView.findViewById(R.id.reloadBTN);

            imageView = itemView.findViewById(R.id.imageView);
            imageSizeMBTV = itemView.findViewById(R.id.imageSizeMBTV);

            progressBar =itemView.findViewById(R.id.progressBar);
            tagsProgressBar =itemView.findViewById(R.id.tagsProgressBar);
            tagsProgressBar2 =itemView.findViewById(R.id.tagsProgressBar2);

            usernameTV = itemView.findViewById(R.id.usernameTV);

            parent =itemView.findViewById(R.id.parent);

            playBTN = itemView.findViewById(R.id.playBTN);
        }
    }

}
