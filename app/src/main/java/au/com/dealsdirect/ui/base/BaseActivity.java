package au.com.dealsdirect.ui.base;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bluelinelabs.conductor.Router;

import au.com.dealsdirect.DDApplication;
import au.com.dealsdirect.R;
import au.com.dealsdirect.di.component.ActivityComponent;
import au.com.dealsdirect.di.component.DaggerActivityComponent;
import au.com.dealsdirect.di.module.ActivityModule;
import au.com.dealsdirect.ui.controller.splash.SplashScreenController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.NetworkUtils;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity implements MvpView {

    private ProgressDialog mProgressDialog;

    private Dialog mLoadingDialog;

    private ActivityComponent mActivityComponent;

    private Unbinder mUnBinder;

    protected Snackbar mSnackbar;

    protected Router mRouter;

    private boolean canShowTimeoutDialog = true;

    private int timeoutDialogDelay = 10000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(((DDApplication) getApplication()).getComponent())
                .build();

        //initial snackbar
        mSnackbar = Snackbar.make(findViewById(android.R.id.content),
                getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE);
    }

    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void showLoading() {
        hideLoading();
        if (!isFinishing() || !isDestroyed()) {
            mProgressDialog = CommonUtils.showLoadingDialog(this);
        }
    }

    @Override
    public void showOurpayLoading() {
        hideOurpayLoading();
        if (!isFinishing() || !isDestroyed()) {
            mProgressDialog = CommonUtils.showLoadingDialogOurpay(this);
        }
    }

    @Override
    public void hideOurpayLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing() && (!isFinishing() || !isDestroyed())) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void showAfterpayLoading() {
        hideAfterpayLoading();
        if (!isFinishing() || !isDestroyed()) {
            mProgressDialog = CommonUtils.showLoadingDialogAfterpay(this);
        }
    }

    @Override
    public void hideAfterpayLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing() && (!isFinishing() || !isDestroyed())) {
            mProgressDialog.cancel();
        }
    }



    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing() && (!isFinishing() || !isDestroyed())) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void showLoadingDialog(String message, boolean cancelable) {
        hideLoadingDialog();
        if (!isFinishing() || !isDestroyed()) {
            mLoadingDialog = CommonUtils.showLoadingDialog(this, message ,cancelable);
        }
    }

    @Override
    public void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing() && (!isFinishing() || !isDestroyed())) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    public void onError(String message) {
        Handler handler = new Handler();
        Log.i("SnackbarError", message + "");
        if(message != null && !message.isEmpty()){
            if(message.contains("UnknownHostException")){
//                if (canShowTimeoutDialog) {
//                    canShowTimeoutDialog = false;
//                    CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, getString(R.string.no_network_connection));
//                    handler.postDelayed(() -> {
//                        canShowTimeoutDialog = true;
//                    }, timeoutDialogDelay);
//                }
                showSnackBar(getString(R.string.no_internet_connection), true);
            } else if (message.contains("SocketTimeoutException") ||
                    message.contains("SSLHandshakeException")) {
                // Do not notify for these errors
                return;
            } else if (message.contains("Null")){
                return;
            } else if (message.contains("Exception") || message.contains("virtual method")) {
//                CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, getString(R.string.error));
                showSnackBar(getString(R.string.error), false);
            }  else if (message.contains("error")) {
                CustomAlertDialog.showCustomAlertDialog(this,
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE, getString(R.string.an_error_has_occurred));
            }
            else {
                /*
                    4/6/18 - feature/andr-3308-registersubscriber
                    Disallow showing of No internet Connection on Socket Timeout Exception
                 */
                CustomAlertDialog.showCustomAlertDialog(this,
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE, message);

            }
        }
    }

    private boolean isCurrentControllerNotSplash(){
        if(mRouter != null && mRouter.hasRootController()){
            if(mRouter.getBackstack().get(0).controller() instanceof SplashScreenController){
                return false;
            }
        }
        return true;
    }

    protected void showSnackBar(String message, boolean indefinite) {
        mSnackbar = Snackbar.make(findViewById(android.R.id.content),
                message, indefinite ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_SHORT);
        View sbView = mSnackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.icon_snack_bar));
        sbView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        sbView.getLayoutParams().height = Math.round(getResources().getDimension(R.dimen.bottom_nav_height));
        TextView textView = (TextView) sbView
                .findViewById(android.support.design.R.id.snackbar_text);

//        support v23 changed behavior for this. ref: https://stackoverflow.com/questions/32668217/android-snackbar-textalignment-in-center
//        changed last 04/18/18
//        Jp/Ayi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        if(isCurrentControllerNotSplash()) {
            mSnackbar.show();
        }
    }

    protected void dismissSnackBar(){
        if(mSnackbar != null) {
            mSnackbar.dismiss();
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        onError(getString(resId));
    }

    @Override
    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }

    public void hideKeyboard() {
//        View view = this.getCurrentFocus();
//        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
//        }
    }

    @Override
    public void onRefreshStart() {
    }

    @Override
    public void onRefreshEnd() {
    }

    public void setUnBinder(Unbinder unBinder) {
        mUnBinder = unBinder;
    }

    @Override
    protected void onDestroy() {

        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }

    protected abstract void setUp();
}
