package com.HQHMA.rule34;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.HQHMA.rule34.Adapters.RecyclerViewAdapterCollections;

import java.util.ArrayList;
import java.util.Arrays;

public class CollectionFragment extends Fragment {

    public RecyclerView recyclerViewCollection;
    private RecyclerView.Adapter mAdapterCollection;
    private RecyclerView.LayoutManager layoutManager;

    private TextView text_no_collection;

    public CollectionFragment() {
        // Required empty public constructor
    }

    public static CollectionFragment newInstance(String param1, String param2) {
        CollectionFragment fragment = new CollectionFragment();
/*        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        text_no_collection = view.findViewById(R.id.text_no_collection);

        loadData(view);

        return view;
    }

    private void loadData(View view) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Rule34",MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        String lastSavedTags = sharedPreferences.getString("collectionTags","");

        if (!lastSavedTags.isEmpty()) {
            loadRecyclerViewTags(view, lastSavedTags);
            text_no_collection.setVisibility(View.GONE);
        }else {
            text_no_collection.setVisibility(View.VISIBLE);
        }

    }

    public void reLoadData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Rule34",MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        String lastSavedTags = sharedPreferences.getString("collectionTags","");

        if (!lastSavedTags.isEmpty()) {
            mAdapterCollection = new RecyclerViewAdapterCollections(new ArrayList<String>(Arrays.asList(lastSavedTags.split("\n"))),CollectionFragment.this, getContext());
            mAdapterCollection.notifyDataSetChanged();
            text_no_collection.setVisibility(View.GONE);
        }else {
            text_no_collection.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void loadRecyclerViewTags(View view, String collections) {
        recyclerViewCollection = view.findViewById(R.id.collectionRecyclerView);
        //recyclerViewTags.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerViewCollection.setLayoutManager(layoutManager);
        mAdapterCollection = new RecyclerViewAdapterCollections(new ArrayList<String>(Arrays.asList(collections.split("\n"))),CollectionFragment.this,getContext());
        recyclerViewCollection.setAdapter(mAdapterCollection);

    }

    @Override
    public void onStart() {
        reLoadData();
        super.onStart();
    }

    @Override
    public void onResume() {
        reLoadData();
        super.onResume();
    }
}