package au.com.dealsdirect.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;

import javax.inject.Inject;

import au.com.dealsdirect.di.component.ControllerComponent;
import au.com.dealsdirect.di.component.DaggerControllerComponent;
import au.com.dealsdirect.di.module.ControllerModule;
import au.com.dealsdirect.service.datacollection.registerservices.FirebaseAnalyticsService;
import au.com.dealsdirect.service.datacollection.registerservices.GenieEventService;
import au.com.dealsdirect.ui.main.MainActivity;


public abstract class BaseController
        extends RefWatchingController
        implements MvpView {

    @Inject
    protected MainActivity mActivity;

    @Inject
    protected GenieEventService mGenieEventService;

    @Inject
    protected FirebaseAnalyticsService mFirebaseEventService;

    private ControllerComponent mControllerComponent;

    protected BaseController() {
    }

    protected BaseController(Bundle args) {
        super(args);
    }

    private ProgressDialog mProgressDialog;

    private boolean mIsViewBound = false;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        setHasOptionsMenu(false);

        mControllerComponent = DaggerControllerComponent.builder()
                .controllerModule(new ControllerModule(this, getActivity()))
                .activityComponent(((BaseActivity) getActivity()).getActivityComponent())
                .build();

        mControllerComponent.inject(this);

        return super.onCreateView(inflater, container);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        if (getActivity() instanceof MainActivity) {
            this.mActivity = (MainActivity) getActivity();
        }
        super.onAttach(view);
    }

    public ControllerComponent getControllerComponent() {
        return mControllerComponent;
    }

    protected abstract void setUp(View view);

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mIsViewBound = true;
    }

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
    public void showOurpayLoading() {
        if (mActivity != null) {
            mActivity.showOurpayLoading();
        }
    }

    @Override
    public void hideOurpayLoading() {
        if (mActivity != null) {
            mActivity.hideOurpayLoading();
            mActivity.updateSnackbar(mActivity.isNetworkConnected());
        }
    }

    @Override
    public void showGPayLoading() {
        if (mActivity != null) {
            mActivity.showGPayLoading();
        }
    }

    @Override
    public void hideGPayLoading() {
        if (mActivity != null) {
            mActivity.hideGPayLoading();
            mActivity.updateSnackbar(mActivity.isNetworkConnected());
        }
    }

    @Override
    public void showAfterpayLoading() {
        if (mActivity != null) {
            mActivity.showAfterpayLoading();
        }
    }

    @Override
    public void hideAfterpayLoading() {
        if (mActivity != null) {
            mActivity.hideAfterpayLoading();
            mActivity.updateSnackbar(mActivity.isNetworkConnected());
        }
    }

    @Override
    public void showLPayLoading() {
        if (mActivity != null) {
            mActivity.showLPayLoading();
        }
    }

    @Override
    public void hideLPayLoading() {
        if (mActivity != null) {
            mActivity.hideLPayLoading();
        }
    }

    @Override
    public void showOpenpayLoading() {
        if (mActivity != null) {
            mActivity.showOpenpayLoading();
        }
    }

    @Override
    public void hideOpenpayLoading() {
        if (mActivity != null) {
            mActivity.hideOpenpayLoading();
        }
    }

    @Override
    public void showLoadingDelayed(int delay) {
        if (mActivity != null) {
            mActivity.showLoadingDelayed(delay);
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
        /* gen-8065_ozsale-reskin_bugfixing - dismiss keyboard when changing screen fix */
        hideKeyboard();
        super.onDetach(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mControllerComponent = null;
        super.onDestroyView(view);
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


    public void onOrientationChanged(Configuration newConfiguration) {

    }

    public void refreshContents() {
        // Override
    }

    public Resources getResource() {
        return mActivity.getResources();
    }

    public int getColor(@ColorRes int resId) {
        if (mActivity == null || mActivity.getResources() == null) {
            return 0;
        }
        return mActivity.getResources().getColor(resId);
    }

    public float getDimension(@DimenRes int resId) {
        if (mActivity == null || mActivity.getResources() == null) {
            return 0;
        }
        return mActivity.getResources().getDimension(resId);
    }

    public String getString(@StringRes int resId) {
        if (mActivity == null || mActivity.getResources() == null) {
            return null;
        }
        return mActivity.getString(resId);
    }

    public Drawable getDrawable(@DrawableRes int resId) {
        if (mActivity == null || mActivity.getResources() == null) {
            return null;
        }
        return mActivity.getDrawable(resId);
    }

    public int getInteger(int resId) {
        if (mActivity == null || mActivity.getResources() == null) {
            return 0;
        }
        return mActivity.getResources().getInteger(resId);
    }

    public boolean getBoolean(int resId) {
        if (mActivity == null || mActivity.getResources() == null) {
            return false;
        }
        return mActivity.getResources().getBoolean(resId);
    }

    public void onViewWillAppear(Controller previousController) {

    }

    public void onViewDidAppear(Controller previousController) {

    }

    public void onViewWillDisappear(Controller nextController) {

    }

    public void onViewDidDisappear(Controller nextController) {

    }

    public void onTabSwitch(boolean intoThisView) {

    }

    public boolean isViewBound() {
        return mIsViewBound;
    }

    public static class CommonControllerChangeListener implements com.bluelinelabs.conductor.ControllerChangeHandler.ControllerChangeListener {

        public static void addToRouter(Router... routers) {
            for (Router router : routers) {
                if (router != null) {
                    router.addChangeListener(CommonControllerChangeListener.getSharedInstance());
                }
            }
        }

        private static CommonControllerChangeListener sharedInstance;

        private static CommonControllerChangeListener getSharedInstance() {
            if (sharedInstance == null) {
                sharedInstance = new CommonControllerChangeListener();
            }

            return sharedInstance;
        }

        @Override
        public void onChangeStarted(@Nullable Controller to,
                                    @Nullable Controller from,
                                    boolean isPush,
                                    @NonNull ViewGroup container,
                                    @NonNull com.bluelinelabs.conductor.ControllerChangeHandler handler) {
            if (from instanceof BaseController) {
                ((BaseController) from).onViewWillDisappear(to);
            }

            if (to instanceof BaseController) {
                ((BaseController) to).onViewWillAppear(from);
            }
        }

        @Override
        public void onChangeCompleted(@Nullable Controller to,
                                      @Nullable Controller from,
                                      boolean isPush,
                                      @NonNull ViewGroup container,
                                      @NonNull com.bluelinelabs.conductor.ControllerChangeHandler handler) {
            if (from instanceof BaseController) {
                ((BaseController) from).onViewDidDisappear(to);
            }

            if (to instanceof BaseController) {
                ((BaseController) to).onViewDidAppear(from);
            }
        }
    }
}
