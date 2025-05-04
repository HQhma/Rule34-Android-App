package com.HQHMA.rule34.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.HQHMA.rule34.FeedFragment;
import com.HQHMA.rule34.Models.Tag;
import com.HQHMA.rule34.R;

import java.util.ArrayList;

public class RecyclerViewAdapterTagsForItems extends RecyclerViewHeightLimitedEdition.Adapter<RecyclerViewAdapterTagsForItems.MyViewHolder> {

    ArrayList<Tag> tags = new ArrayList<>();
    Context context;
    FeedFragment fragment;
    RecyclerView recyclerView;

    public RecyclerViewAdapterTagsForItems(ArrayList<Tag> tags, Context context, FeedFragment feedFragment,RecyclerView recyclerView) {
        this.tags = tags;
        this.context = context;
        this.fragment = feedFragment;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Tag thisTag = tags.get(position);

        holder.textView.setText(thisTag.getValue());

        //getTagTypes(thisTag.getValue(),holder,position);

        holder.parent.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(thisTag.getTypeColor())));
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.setSearchView(thisTag);
            }
        });
        holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                thisTag.setValue("-" + thisTag.getValue());
                fragment.setSearchView(thisTag);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ProgressBar progressBar;
        CardView parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            parent = itemView.findViewById(R.id.parent);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

}
