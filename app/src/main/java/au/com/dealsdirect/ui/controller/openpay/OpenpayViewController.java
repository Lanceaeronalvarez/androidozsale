package au.com.dealsdirect.ui.controller.openpay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.login.PopUpHostController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

public class OpenpayViewController extends BaseController implements OpenpayMvpView {

    @Inject
    OpenpayMvpPresenter<OpenpayMvpView> mPresenter;

    @BindView(R.id.controller_openpay_webview)
    WebView mWebView;

    private String planId = null;
    private String orderId = null;
    private String postUrl = null;

    private EventListener eventListener = null;

    public OpenpayViewController(Bundle args) {
        super(args);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        mActivity.getMainController().hideBottomNav();

        if (planId == null || planId.isEmpty() ||
                orderId == null || orderId.isEmpty() ||
                postUrl == null || postUrl.isEmpty()) {
            if (!mPresenter.isBusy()) {
                mPresenter.createOpenpayOrder();
            }
        } else {
            showOpenpayWebView(planId, orderId, postUrl);
        }

        super.onAttach(view);
    }

    @Override
    public void onDetach(View view) {
        mActivity.getMainController().showBottomNav();

        mPresenter.onDetach();
        super.onDetach(view);
    }

    @Override
    protected void setUp(View view) {
        CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        hideOpenpayWebView();
        hideProgressIndicator();
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_openpay, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void showOpenpayWebView(String planId, String orderId, String postUrl) {
        if (mWebView.getUrl() == null || mWebView.getUrl().equals("about:blank")) {
            this.planId = planId;
            this.orderId = orderId;
            this.postUrl = postUrl;
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    final Uri uri = request.getUrl();
                    if (!uri.toString().contains(mPresenter.getRedirectUrlPrefix())) {
                        return false;
                    }

                    final String status = uri.getQueryParameter("status");
                    final boolean isValidated = validateQuery(uri);

                    if (isValidated) {
                        handleStatus(status);
                    } else {
                        showError();
                    }

                    hideOpenpayWebView();

                    return true;
                }
            });
            mWebView.loadUrl(postUrl);
        }

        mWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideOpenpayWebView() {
        mWebView.setVisibility(View.GONE);
    }

    @Override
    public void showProgressIndicator() {
        showOpenpayLoading();
    }

    @Override
    public void hideProgressIndicator() {
        hideOpenpayLoading();
    }

    @Override
    public void showPaymentSuccess(String address, Double price, Double shipping, String invoice, String delivery) {
        BundleBuilder bundleBuilder = new BundleBuilder(new Bundle())
                .putString(BundleKeys.KEY_ADDRESS, address)
                .putDouble(BundleKeys.KEY_PRICE, price)
                .putDouble(BundleKeys.KEY_SHIPPING_FEE, shipping)
                .putString(BundleKeys.KEY_INVOICE, invoice)
                .putString(BundleKeys.KEY_ESTIMATED_DELIVERY, delivery);

        if (mPresenter.isTablet()) {
            Router router = mActivity.getMainController().getPopUpHostRouter();
            Bundle bundle = bundleBuilder
                    .putSerializable(BundleKeys.KEY_POP_UP_HOST_DESTINATION, GateKeeper.Destination.PAYMENT_SUCCESS)
                    .build();

            RouterTransaction routerTransaction = RouterTransaction
                    .with(new PopUpHostController(bundle))
                    .pushChangeHandler(new FadeChangeHandler())
                    .popChangeHandler(new FadeChangeHandler());

            if (getRouter() == router) {
                router.replaceTopController(routerTransaction);
            } else {
                dismissSelf();
                router.pushController(routerTransaction);
            }
        } else {
            dismissSelf();
            mActivity.getCheckoutController().getRouter()
                    .pushController(RouterTransaction
                            .with(new PaymentSuccessController(bundleBuilder.build()))
                            .pushChangeHandler(new HorizontalChangeHandler())
                            .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    @Override
    public void showError(String message) {
        hideOpenpayWebView();
        hideProgressIndicator();

        if (message == null || message.isEmpty()) {
            showAlertDialog(mActivity.getResources()
                    .getString(R.string.openpay_failed_transaction));
        } else {
            showAlertDialog(message);
        }
    }

    private void showError() {
        showError(null);
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(mActivity)
                .setTitle(mActivity.getResources().getString(R.string.openpay))
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setOnDismissListener(dialog -> dismissSelf())
                .show();
    }

    private void dismissSelf() {
        mActivity.onBackPressed();
    }

    public boolean isBusy() {
        return mPresenter.isBusy();
    }

    private void handleStatus(String status) {
        if (status == null) {
            return;
        }

        switch (status) {
            case "LODGED":
            case "FAILED":
                mPresenter.capturePaymentRequest(planId, orderId, status);
                break;
            default:
                dismissSelf();
                break;
        }
    }


    private boolean validateQuery(Uri uri) {
        if (planId == null || planId.isEmpty() ||
                orderId == null || orderId.isEmpty()) {
            return false;
        }

        final String queryPlanId = uri.getQueryParameter("planid");
        final String queryOrderId = uri.getQueryParameter("orderid");

        return planId.equals(queryPlanId) && orderId.equals(queryOrderId);
    }

    private void logError(String errorMessage) {
        if (eventListener != null) {
            eventListener.onError(errorMessage);
        }
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public interface EventListener {
        void onError(String errorMessage);
    }
}
