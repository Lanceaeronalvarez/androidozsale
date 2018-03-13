package au.com.dealsdirect.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import au.com.dealsdirect.di.component.ControllerComponent;
import au.com.dealsdirect.di.component.DaggerControllerComponent;
import au.com.dealsdirect.di.module.ControllerModule;
import au.com.dealsdirect.ui.main.MainActivity;


public abstract class BaseController extends RefWatchingController implements MvpView {

    @Inject
    protected MainActivity mActivity;

    private ControllerComponent mControllerComponent;

    protected BaseController() {
    }

    protected BaseController(Bundle args) {
        super(args);
    }

    private ProgressDialog mProgressDialog;
    
    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        setHasOptionsMenu(false);

        mControllerComponent = DaggerControllerComponent.builder()
                .controllerModule(new ControllerModule(this))
                .activityComponent(((BaseActivity) getActivity()).getActivityComponent())
                .build();
        mControllerComponent.inject(this);

        return super.onCreateView(inflater, container);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
    }

    public ControllerComponent getControllerComponent() {
        return mControllerComponent;
    }

    protected abstract void setUp(View view);

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
        mActivity = (MainActivity) activity;
    }

    @Override
    public void showLoading() {
        if (mActivity != null) {
            mActivity.showLoading();
        }
    }

    @Override
    public void hideLoading() {
        if (mActivity != null) {
            mActivity.hideLoading();
            mActivity.updateSnackbar(mActivity.isNetworkConnected());
        }
    }

    @Override
    public void showLoadingDialog(String message, boolean cancelable) {
        if (mActivity != null) {
            mActivity.showLoadingDialog(message, cancelable);
        }
    }

    @Override
    public void hideLoadingDialog() {
        if (mActivity != null) {
            mActivity.hideLoadingDialog();
        }
    }

    public void showProgressDialog(String message) {

        hideProgressDialog();

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }
    @Override
    public void onError(String message) {
        if (mActivity != null) {
            mActivity.onError(message);
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        if (mActivity != null) {
            mActivity.onError(resId);
        }
    }

    @Override
    public boolean isNetworkConnected() {
        if (mActivity != null) {
            return mActivity.isNetworkConnected();
        }
        return false;
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
    }

    @Override
    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }

    @Override
    public void onRefreshEnd() {
    }

    @Override
    public void onRefreshStart() {
        showLoading();
        hideNoNetworkLayout();
    }

    @Override
    public void hideNoNetworkLayout() {

    }

    @Override
    public void showNoNetworkLayout() {

    }

    @Override
    public boolean isViewAttached() {
        return isAttached();
    }

    public Resources getResource() { return mActivity.getResources(); }

}
