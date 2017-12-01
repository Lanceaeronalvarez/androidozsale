package au.com.dealsdirect.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mysale.genie.views.custom.CoordinatorLayoutAsBottomSheetBehavior;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smartwave on 24/10/2017.
 */

public abstract class SwipeableBaseToolBarController extends BaseController {

    @BindView(R.id.root_base_toolbar_layout)
    RelativeLayout mRootLayout;

    @BindView(R.id.custom_bottom_button)
    Button mBottomButton;

    @BindView(R.id.swipeable_fragment_bottom_layout)
    FrameLayout mBottomLayout;

    @BindView(R.id.swipeable_fragment_coordinator_layout)
    com.mysale.genie.views.custom.BottomSheetCoordinatorLayout mBottom;

    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar mToolbar;

    CoordinatorLayoutAsBottomSheetBehavior mBottomSheetBehavior;

    FrameLayout mContentLayout;

    LinearLayout mNoNetworkLayout;

    protected TextView mToolbarTitle;

    protected SwipeableBaseToolBarController() {
    }

    protected SwipeableBaseToolBarController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_swipeable_base_toolbar, container, false);

        mContentLayout = view.findViewById(R.id.controller_base_content_layout);

        mNoNetworkLayout = view.findViewById(R.id.no_network_layout);

        mToolbarTitle = view.findViewById(R.id.toolbar_title);

        mNoNetworkLayout.setOnClickListener(v -> onRefreshStart());

        return view;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
    }

    protected void fillContent(View view) {
        mContentLayout.addView(view);

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

    public void showNoNetworkLayout() {
        mContentLayout.setVisibility(View.GONE);
        mNoNetworkLayout.setVisibility(View.VISIBLE);
    }

    public void hideNoNetworkLayout() {
        if (mContentLayout != null && mNoNetworkLayout != null) {
            mContentLayout.setVisibility(View.VISIBLE);
            mNoNetworkLayout.setVisibility(View.GONE);
        }
    }

    public void showNoInternetSnackBar() {
        if (mActivity != null) {
            mActivity.showSnackBar(mActivity.getString(R.string.no_internet_connection), true);
        }
    }

    public void setupDefaultBottomButton(String text,View.OnClickListener listener){
        showBottomLayout();
        mBottomButton.setVisibility(View.VISIBLE);
        mBottomButton.setText(text);
        mBottomButton.setOnClickListener(listener);

    }

    public void showBottomLayout(){
        mBottomLayout.setVisibility(View.VISIBLE);
    }

    public void setupSwipingBehavior() {

        mBottomSheetBehavior = CoordinatorLayoutAsBottomSheetBehavior.from(mRootLayout);

        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setPeekHeight(0);
            mBottomSheetBehavior.setBottomSheetCallback(new CoordinatorLayoutAsBottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case CoordinatorLayoutAsBottomSheetBehavior.STATE_COLLAPSED:
                            mActivity.onBackPressed();

                            break;
                        case CoordinatorLayoutAsBottomSheetBehavior.STATE_EXPANDED:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                    bottomSheet.setAlpha(slideOffset);
                }
            });

        } else {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mRootLayout.getLayoutParams();
            params.setBehavior(new CoordinatorLayoutAsBottomSheetBehavior());
            mBottomLayout.requestLayout();
        }

        mBottomSheetBehavior.setState(CoordinatorLayoutAsBottomSheetBehavior.STATE_EXPANDED);

    }

    protected void disableSwipingBehavior() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mRootLayout.getLayoutParams();
        params.setBehavior(null);
        mBottomLayout.requestLayout();
    }

    @OnClick(R.id.toolbar)
    public void onToolbarTitleClick(){
        mActivity.onBackPressed();
    }


}