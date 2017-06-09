package au.com.dealsdirect.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.dealsdirect.R;
import au.com.dealsdirect.di.component.ControllerComponent;
import au.com.dealsdirect.di.component.DaggerControllerComponent;
import au.com.dealsdirect.di.module.ControllerModule;
import butterknife.BindView;


public abstract class BaseController extends RefWatchingController implements MvpView {

    private BaseActivity mActivity;

    private ControllerComponent mControllerComponent;

    protected BaseController() {
    }

    protected BaseController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        setHasOptionsMenu(false);
        mControllerComponent = DaggerControllerComponent.builder()
                .controllerModule(new ControllerModule(this))
                .activityComponent(((BaseActivity) getActivity()).getActivityComponent())
                .build();
        return super.onCreateView(inflater, container);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        if (getActivity() instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) getActivity();
            this.mActivity = activity;
        }
        super.onAttach(view);
    }

    public ControllerComponent getControllerComponent() {
        return mControllerComponent;
    }

    protected abstract void setUp(View view);

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
        }
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
        mActivity = null;
        super.onDetach(view);
    }

    @Override
    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }
}
