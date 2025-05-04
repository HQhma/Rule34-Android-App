package com.HQHMA.rule34.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.HQHMA.rule34.FeedFragment;
import com.HQHMA.rule34.Models.Tag;
import com.HQHMA.rule34.R;

import java.util.ArrayList;

public class RecyclerViewAdapterSearchSuggests extends RecyclerView.Adapter<RecyclerViewAdapterSearchSuggests.MyViewHolder> {

    ArrayList<Tag> tags = new ArrayList<>();
    Context context;
    FeedFragment feedFragment;

    public RecyclerViewAdapterSearchSuggests(ArrayList<Tag> tags, Context context,FeedFragment feedFragment) {
        this.tags = tags;
        this.context = context;
        this.feedFragment = feedFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_suggest_row,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Tag thisTag = tags.get(position);

        holder.tagNameTV.setText(thisTag.getValue());
        holder.tagTypeTV.setText(thisTag.getType());
        holder.tagTypeTV.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(thisTag.getTypeColor())));
        holder.tagCountTV.setText(thisTag.getCount().toString());

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedFragment.setSearchView(thisTag);
            }
        });
        holder.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisTag.setValue("-" + thisTag.getValue());
                feedFragment.setSearchView(thisTag);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tagNameTV,tagTypeTV,tagCountTV;
        View closeBtn,parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tagNameTV = itemView.findViewById(R.id.tagNameTV);
            tagTypeTV = itemView.findViewById(R.id.tagTypeTV);
            tagCountTV = itemView.findViewById(R.id.tagCountTV);
            closeBtn = itemView.findViewById(R.id.closeBTN);
            parent = itemView.findViewById(R.id.parent);

        }
    }

}
