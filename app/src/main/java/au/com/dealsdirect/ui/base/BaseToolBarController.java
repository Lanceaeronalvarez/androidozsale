package au.com.dealsdirect.ui.base;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.core.widget.TextViewCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;

/**
 * Created by smartwave on 24/10/2017.
 */

public abstract class BaseToolBarController extends BaseController {

    FrameLayout mContentLayout;

    LinearLayout mNoNetworkLayout;

    protected TextView mToolbarTitle;

    protected BaseToolBarController() {
    }

    protected BaseToolBarController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_base_toolbar, container, false);

        mContentLayout = (FrameLayout) view.findViewById(R.id.controller_base_content_layout);

        mNoNetworkLayout = (LinearLayout) view.findViewById(R.id.no_network_layout);

        mToolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);

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


}