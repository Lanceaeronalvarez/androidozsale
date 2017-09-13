package au.com.dealsdirect.ui.base;
/*
 * Created by CodeineBot on 7/31/17.
 */


import android.content.res.ColorStateList;
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
import android.widget.LinearLayout;

import au.com.dealsdirect.R;
import butterknife.BindView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public abstract class BasePullToRefreshController extends BaseController implements PullToRefreshMvpView, PtrHandler {

    FrameLayout mToolbarFrameLayout;

    FrameLayout mContentLayout;

    LinearLayout mNoNetworkLayout;

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

        mNoNetworkLayout = (LinearLayout) view.findViewById(R.id.no_network_layout);

        mNoNetworkLayout.setOnClickListener(v -> onRefreshStart());

        mPtrLayout = (PtrClassicFrameLayout) view.findViewById(R.id.controller_base_ptr_layout);

        mPtrLayout.setPtrHandler(this);

        mPtrLayout.getHeader().setProgressIcon(getResources().getDrawable(R.drawable.ic_loader_logo));

        mPtrLayout.getHeader().setPullProgressbar(getResources().getDrawable(R.drawable.bg_progress_bar));

        mPtrLayout.getHeader().setProgressBar(ColorStateList.valueOf(getResources().getColor(R.color.progress_loader_stroke_color)));

        return view;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPtrLayout.setIsChildScrollingEnabled(false);
    }

    protected void fillContent(View view) {
        mContentLayout.addView(view);
        addOverScrollListener(mContentLayout);
    }

    protected void fillToolbar(View view) {
        mToolbarFrameLayout.addView(view);
    }

    protected View getToolbar() {
        return mToolbarFrameLayout.getChildAt(0).getRootView();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        onRefreshEnd();
    }

    @Override
    public void onError(String message) {
        if (message != null && message.contains("UnknownHostException")) {
            showNoNetworkLayout();
        } else {
            super.onError(message);
        }
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
    public void showNoNetworkLayout() {
        mContentLayout.setVisibility(View.GONE);
        mNoNetworkLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoNetworkLayout() {
        if (mContentLayout != null && mNoNetworkLayout != null) {
            mContentLayout.setVisibility(View.VISIBLE);
            mNoNetworkLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    private void addOverScrollListener(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            if (child instanceof ViewGroup) {
                if (child instanceof RecyclerView) {
                    recyclerViewEnablePullToRefresh(child);
                }
                if (child instanceof NestedScrollView) {
                    nestedScrollViewEnablePullToRefresh(child);
                }
                addOverScrollListener((ViewGroup) child);
            }
        }
    }

    private void recyclerViewEnablePullToRefresh(View child) {
        RecyclerView recyclerView = ((RecyclerView) child);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (((LinearLayoutManager) recyclerView.getLayoutManager()).
                        findFirstCompletelyVisibleItemPosition() == 0) {
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
                if (view.getTop() == scrollY) {
                    mPtrLayout.setIsChildScrollingEnabled(true);
                } else {
                    mPtrLayout.setIsChildScrollingEnabled(false);
                }

            }
        });
    }
}
