package au.com.dealsdirect.ui.controller.zippay;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
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
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.LoadingDialogType;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

public class ZipPayViewController extends BaseController implements ZipPayMvpView {

    @Inject
    ZipPayMvpPresenter<ZipPayMvpView> mPresenter;

    @BindView(R.id.controller_zippay_webview)
    WebView mWebView;

    private EventListener eventListener = null;

    public ZipPayViewController(Bundle args) {
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
        mWebView.setVisibility(View.GONE);

        mWebView.setWebContentsDebuggingEnabled(true);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mPresenter.createOrder(getRedirectUrl().toString());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_zippay, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void showProgressIndicator() {
        showLoading(LoadingDialogType.ZIPPAY);
    }

    @Override
    public void hideProgressIndicator() {
        hideLoading();
    }

    @Override
    public void showPaymentSuccess(String address, Double price, Double shipping, String invoice, String delivery) {
        Log.d(ZipPayViewController.class.getSimpleName(), "ZipPaymentView.showPaymentSuccess");
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
            mActivity.getCurrentRouter()
                    .pushController(RouterTransaction
                            .with(new PaymentSuccessController(bundleBuilder.build()))
                            .pushChangeHandler(new HorizontalChangeHandler())
                            .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    @Override
    public void showError(String message) {
        mWebView.setVisibility(View.GONE);
        hideProgressIndicator();

        if (message == null || message.isEmpty()) {
            showAlertDialog(mActivity.getResources()
                    .getString(R.string.zippay_failed_transaction));
        } else {
            showAlertDialog(message);
        }
    }

    private void showError() {
        showError(null);
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(mActivity)
                .setTitle(mActivity.getResources().getString(R.string.zippay))
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

    private Uri getRedirectUrl() {
        return Uri.parse(Settings.getSelectedCountry().legacyRoot);
    }

    @Override
    public void receivedAUOrderRequest(String redirectUrl) {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final String host = request.getUrl().getHost();

                if (host == null || !host.equalsIgnoreCase(getRedirectUrl().getHost())) {
                    return false;
                }

                final String checkoutId = request.getUrl().getQueryParameter("checkoutId");
                final String result = request.getUrl().getQueryParameter("result");
                final String customerId = request.getUrl().getQueryParameter("customerId");

                if (checkoutId == null || checkoutId.isEmpty() || result == null) {
                    logError("Payment Error, URL" + request.getUrl().toString());
                    showError();
                    mWebView.setVisibility(View.GONE);
                    return true;
                }

                if (result.equalsIgnoreCase("cancelled")) {
                    dismissSelf();
                    return true;
                }
                if (result.equalsIgnoreCase("approved")) {
                    mPresenter.createAUCharge(checkoutId, checkoutId, result);
                } else {
                    logError("Payment not approved: " + result);
                    showError();
                }
                mWebView.setVisibility(View.GONE);
                return true;
            }
        });

        mWebView.loadUrl(redirectUrl);
        mWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void receivedNZOrderRequest(String redirectUri) {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final String host = request.getUrl().getHost();

                if (host == null || !host.equalsIgnoreCase(getRedirectUrl().getHost())) {
                    return false;
                }

                final String orderId = request.getUrl().getQueryParameter("orderId");
                final String paymentStatus = request.getUrl().getQueryParameter("paymentStatus");
                final String token = request.getUrl().getQueryParameter("token");

                if (orderId == null || orderId.isEmpty() ||
                        token == null || token.isEmpty() ||
                        paymentStatus == null) {
                    logError("Payment Error, URL" + request.getUrl().toString());
                    showError();
                    mWebView.setVisibility(View.GONE);
                    return true;
                }

                if (paymentStatus.equalsIgnoreCase("cancelled")) {
                    dismissSelf();
                    return true;
                }
                if (paymentStatus.equalsIgnoreCase("success")) {
                    mPresenter.confirmNZOrder("success", orderId, token);
                } else {
                    logError("Payment failed, status: " + paymentStatus);
                    showError();
                }
                mWebView.setVisibility(View.GONE);
                return true;
            }
        });

        mWebView.loadUrl(redirectUri);
        mWebView.setVisibility(View.VISIBLE);
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
