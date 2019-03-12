package ru.cloudpayments.demo.support;

import android.content.Context;
import android.graphics.Rect;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

public class SideSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private float spacingInDP;
    private int spanCount;
    private boolean includeEdge;

    public SideSpaceItemDecoration(Context context, int spacing, int spanCount, boolean includeEdge) {

        spacingInDP = spacing * context.getResources().getDisplayMetrics().density;
        this.spanCount = spanCount;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;

        if (includeEdge) {
            outRect.left = (int) (spacingInDP - column * spacingInDP / spanCount);
            outRect.right = (int) ((column + 1) * spacingInDP / spanCount);

            if (position < spanCount) {
                outRect.top = (int) spacingInDP;
            }
            outRect.bottom = (int) spacingInDP;
        } else {
            outRect.left = (int) (column * spacingInDP / spanCount);
            outRect.right = (int) (spacingInDP - (column + 1) * spacingInDP / spanCount);
            if (position >= spanCount) {
                outRect.top = (int) spacingInDP;
            }
        }
    }
}
