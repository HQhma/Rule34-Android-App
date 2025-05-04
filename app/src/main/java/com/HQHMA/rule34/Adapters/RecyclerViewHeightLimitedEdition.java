package com.HQHMA.rule34.Adapters;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.HQHMA.rule34.Utilities.Utilities;

public class RecyclerViewHeightLimitedEdition extends RecyclerView {

    Context context;

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int newRecyclerViewHeight = MeasureSpec.getSize(heightSpec);;
        int displayHeight = Utilities.getDisplayHeight(context);
        if (newRecyclerViewHeight > displayHeight/1.3)
            newRecyclerViewHeight = (int) (displayHeight/1.3);

        heightSpec = MeasureSpec.makeMeasureSpec(newRecyclerViewHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }

    public RecyclerViewHeightLimitedEdition(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public RecyclerViewHeightLimitedEdition(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public RecyclerViewHeightLimitedEdition(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }
}
