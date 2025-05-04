package com.HQHMA.rule34.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.HQHMA.rule34.FeedFragment;
import com.HQHMA.rule34.Models.Tag;
import com.HQHMA.rule34.R;
import com.HQHMA.rule34.Utilities.UrlUtilities;

import java.util.ArrayList;

public class RecyclerViewAdapterTags extends RecyclerView.Adapter<RecyclerViewAdapterTags.MyViewHolder> {

    ArrayList<Tag> tags = new ArrayList<>();
    Context context;
    FeedFragment fragment;

    public RecyclerViewAdapterTags(ArrayList<Tag> tags, Context context, FeedFragment feedFragment) {
        this.tags = tags;
        this.context = context;
        this.fragment = feedFragment;
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
        holder.parent.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(thisTag.getTypeColor())));
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tags.remove(position);
                notifyDataSetChanged();
                fragment.reupdate_theJSON(UrlUtilities.getUrl(fragment.getSearchText(false),context));
                fragment.divider_state();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        CardView parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            parent = itemView.findViewById(R.id.parent);
        }
    }

}
