package com.HQHMA.rule34.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.HQHMA.rule34.CollectionFragment;
import com.HQHMA.rule34.FeedFragment;
import com.HQHMA.rule34.MainActivity;
import com.HQHMA.rule34.Models.Tag;
import com.HQHMA.rule34.R;

import java.util.ArrayList;

public class RecyclerViewAdapterCollections extends RecyclerView.Adapter<RecyclerViewAdapterCollections.MyViewHolder> {

    Context context;
    //String[] collections;
    CollectionFragment collectionFragment;
    ArrayList<String> collections = new ArrayList<>();

    public RecyclerViewAdapterCollections(ArrayList<String> collections,CollectionFragment collectionFragment, Context context) {
        this.collections = collections;
        this.collectionFragment = collectionFragment;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_row,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String[] collectionThings = collections.get(position).split(";");
        String[] tagsNameAndType = collectionThings[1].split(" "); // collection items

        holder.tagCollectionNameTV.setText(collectionThings[0]); // collection name

        holder.tags.clear();
        for (String tag:tagsNameAndType){
            String[] tempTag = tag.split(":");
            holder.tags.add(new Tag(tempTag[0],0L,0L,Integer.parseInt(tempTag[1])));
        }
        holder.mAdapterTags = new RecyclerViewAdapterTagsForCollections(holder.tags,context);
        holder.recyclerViewTags.setAdapter(holder.mAdapterTags);

        holder.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFeedFragment(holder.tags);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete item row
                SharedPreferences sharedPreferences = context.getSharedPreferences("Rule34",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String lastSavedTags = sharedPreferences.getString("collectionTags","");

                Log.i("TAG123", lastSavedTags);
                String newSavedTags;
                if (collections.size() == 1){
                    newSavedTags = null;
                } else if (position == collections.size()-1){ // last item
                    newSavedTags =  lastSavedTags.replace("\n" + collections.get(position),"");
                } else{ // item was the first one or somewhere else
                    newSavedTags = lastSavedTags.replace(collections.get(position) + "\n","");
                }
                //Log.i("TAG123", newSavedTags);
                editor.putString("collectionTags",newSavedTags);
                editor.apply();

                holder.recyclerViewTags.setAdapter(null);

                collections.remove(position);
                notifyDataSetChanged();

                if (collections.size() == 0){
                    collectionFragment.reLoadData();
                }
            }
        });

    }

    private void openFeedFragment(ArrayList<Tag> tags) {

        MainActivity mainActivity = (MainActivity) context;

        Bundle bundle = new Bundle();
        String tagsAsString="";
        for (Tag tag:tags) {
            String valueOfTag = tag.getValue() + ":" + tag.getTypeIndex();
            if (tagsAsString.isEmpty())
                tagsAsString = valueOfTag;
            else
                tagsAsString += " " + valueOfTag;
        }
        bundle.putString("tags",tagsAsString);
        FeedFragment feedFragment = new FeedFragment();
        feedFragment.setArguments(bundle);
        mainActivity.feedFragment = feedFragment;
        mainActivity.buttonNavView.setSelectedItemId(R.id.menu_Feed);
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerViewTags;
        RecyclerView.Adapter mAdapterTags;
        RecyclerView.LayoutManager layoutManager;
        ArrayList<Tag> tags = new ArrayList<>();
        TextView tagCollectionNameTV;

        Button searchBtn,deleteBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerViewTags = itemView.findViewById(R.id.tagsRecyclerView);
            layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
            recyclerViewTags.setLayoutManager(layoutManager);
            searchBtn = itemView.findViewById(R.id.searchBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            tagCollectionNameTV = itemView.findViewById(R.id.tagCollectionName);
        }
    }

}
