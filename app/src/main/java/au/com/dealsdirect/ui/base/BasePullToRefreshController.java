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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import au.com.dealsdirect.R;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;



public abstract class BasePullToRefreshController extends BaseController implements PullToRefreshMvpView, PtrHandler {

    protected enum ToolBarType {

        LOGO, ARROW, LOGIN, TITLE;

        int getLayout() {
            switch (this) {
                case LOGO:
                    return R.layout.partial_toolbar_logo;
                case LOGIN:
                    return R.layout.partial_toolbar_login;
                case TITLE:
                    return R.layout.partial_toolbar_title;
                default:
                    return R.layout.partial_toolbar_arrow;
            }
        }
    }

    private LinearLayout mSearchBarLayout;

    protected EditText mSearchBarEditText;

    FrameLayout mToolbarFrameLayout;

    FrameLayout mContentLayout;

    LinearLayout mNoNetworkLayout;

    PtrClassicFrameLayout mPtrLayout;

    private View mToolBarView;

    boolean mCanDoRefresh = true;

    protected BasePullToRefreshController() {
    }

    protected BasePullToRefreshController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflateView(inflater, container, ToolBarType.ARROW);
    }

    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, ToolBarType type) {
        View view = inflater.inflate(R.layout.controller_base_ptr, container, false);
        bindPtrViews(view);
        fillToolbar(inflater.inflate(type.getLayout(), container, false));
        setSearchBarVisible(false);
        return view;
    }

    public void setToolBarVisible(boolean isVisible) {
        setViewVisible(mToolBarView, isVisible);
    }

    public void setSearchBarVisible(boolean isVisible) {
        setViewVisible(mSearchBarLayout, isVisible);
    }

    private void setViewVisible(View view, boolean isVisible) {
        int state = isVisible ? View.VISIBLE : View.GONE;
        view.setVisibility(state);
    }

    protected void bindPtrViews(View view){

        mSearchBarLayout = (LinearLayout) view.findViewById(R.id.controller_base_search_layout);

        mSearchBarEditText = (EditText) view.findViewById(R.id.controller_base_search_edittext);

        mToolbarFrameLayout = (FrameLayout) view.findViewById(R.id.controller_base_toolbar_layout);

        mContentLayout = (FrameLayout) view.findViewById(R.id.controller_base_content_layout);

        mNoNetworkLayout = (LinearLayout) view.findViewById(R.id.no_network_layout);

        mNoNetworkLayout.setOnClickListener(v -> onRefreshStart());

        mPtrLayout = (PtrClassicFrameLayout) view.findViewById(R.id.controller_base_ptr_layout);

        mPtrLayout.setPtrHandler(this);

        mPtrLayout.getHeader().setProgressIcon(getResources().getDrawable(R.drawable.ic_loader_logo));

        mPtrLayout.getHeader().setPullProgressbar(getResources().getDrawable(R.drawable.bg_progress_bar));

        mPtrLayout.getHeader().setProgressBar(ColorStateList.valueOf(getResources().getColor(R.color.progress_loader_stroke)));

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

    private void fillToolbar(View view) {
        mToolBarView = view;
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
        }

        super.onError(message);
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
    public void showNoInternetSnackBar() {
        if (mActivity != null) {
            mActivity.showSnackBar(mActivity.getString(R.string.no_internet_connection), true);
        }
    }

    @Override
    public void dismissSnackBar() {
        if (mActivity != null) {
            mActivity.dismissSnackBar();
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return mCanDoRefresh && PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    protected void enablePullToRefresh(boolean val){
        mCanDoRefresh = val;
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
                        findFirstCompletelyVisibleItemPosition() == 0
                        || ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation() ==
                        LinearLayoutManager.HORIZONTAL) {
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
