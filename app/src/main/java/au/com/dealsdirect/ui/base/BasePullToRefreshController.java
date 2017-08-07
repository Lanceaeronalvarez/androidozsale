package au.com.dealsdirect.ui.base;
/*
 * Created by CodeineBot on 7/31/17.
 */


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import au.com.dealsdirect.R;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public abstract class BasePullToRefreshController extends BaseController implements MvpView, PtrHandler {

    FrameLayout mToolbarFrameLayout;

    FrameLayout mContentLayout;

    PtrClassicFrameLayout mPtrLayout;

    protected BasePullToRefreshController() {
    }

    protected BasePullToRefreshController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_base, container, false);

        mToolbarFrameLayout = (FrameLayout) view.findViewById(R.id.controller_base_toolbar_layout);

        mContentLayout = (FrameLayout) view.findViewById(R.id.controller_base_content_layout);

        mPtrLayout = (PtrClassicFrameLayout) view.findViewById(R.id.controller_base_ptr_layout);
        mPtrLayout.setPtrHandler(this);

        return view;
    }

    protected void fillContent(View view) {
        mContentLayout.addView(view);
        addOverScrollListener(mContentLayout);
    }

    protected void fillToolbar(View view) {
        mToolbarFrameLayout.addView(view);
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        onRefreshEnd();
    }

    @Override
    public void onRefreshStart() {

    }

    @Override
    public void onRefreshEnd() {
        if (mPtrLayout != null) {
            mPtrLayout.setLastUpdateTimeRelateObject(this);
            mPtrLayout.refreshComplete();
        }
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        onRefreshStart();
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    private void addOverScrollListener(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            Log.d("PullToRefresh",child+" ");
            if (child instanceof ViewGroup){
                if(child instanceof RecyclerView) {
                    recyclerViewEnablePullToRefresh(child);
                }
                if(child instanceof NestedScrollView){
                    nestedScrollViewEnablePullToRefresh(child);
                }
                addOverScrollListener((ViewGroup)child);
            }
        }
    }

    private void recyclerViewEnablePullToRefresh(View child) {
        RecyclerView recyclerView = ((RecyclerView)child);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(((LinearLayoutManager) recyclerView.getLayoutManager()).
                        findFirstCompletelyVisibleItemPosition() == 0){
                    mPtrLayout.setIsChildScrollingEnabled(true);
                } else {
                    mPtrLayout.setIsChildScrollingEnabled(false);
                }
            }
        });
    }

    private void nestedScrollViewEnablePullToRefresh(View child) {
        NestedScrollView nestedScrollView = ((NestedScrollView) child);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(view.getTop()==scrollY){
                    mPtrLayout.setIsChildScrollingEnabled(true);
                } else {
                    mPtrLayout.setIsChildScrollingEnabled(false);
                }

            }
        });
    }
}
