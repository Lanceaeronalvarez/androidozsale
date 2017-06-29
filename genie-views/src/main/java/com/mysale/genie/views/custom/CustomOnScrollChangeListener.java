package com.mysale.genie.views.custom;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.mysale.genie.views.custom.recyclerview.CustomGridLayoutManager;
import com.mysale.genie.views.custom.recyclerview.EndlessRecyclerViewScrollListener;


/**
 * Created by smartwave on 04/01/2017.
 */

public abstract class CustomOnScrollChangeListener implements NestedScrollView.OnScrollChangeListener {

    private RecyclerView mRecyclerview;
    private RecyclerView.LayoutManager mLayoutManager;

    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    public CustomOnScrollChangeListener(RecyclerView rv){
        mRecyclerview=rv;
        this.mLayoutManager = rv.getLayoutManager();
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        int distanceToEnd = (mRecyclerview.getBottom() - (v.getHeight() + v.getScrollY()));

        double distanceThreshold = mRecyclerview.findViewHolderForLayoutPosition(0).itemView.getHeight() * 1.3;

        int totalItemCount = mLayoutManager.getItemCount();

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            Log.d("loading",loading+"");
            previousTotalItemCount = totalItemCount;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && distanceToEnd < (int)distanceThreshold) {
            Log.d("loading",!loading+"");
            currentPage++;
            onLoadMore(currentPage, totalItemCount);
            Log.d(CustomOnScrollChangeListener.class.getName(),"onLoadMoreCalled");
            loading = true;
        }

    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page, int totalItemsCount);
}
