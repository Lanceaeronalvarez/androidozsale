package au.com.dealsdirect.ui.controller.lpay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
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

public class LPayViewController extends BaseController implements LPayMvpView {

    @Inject
    LPayMvpPresenter<LPayMvpView> mPresenter;

    @BindView(R.id.controller_lpay_webview)
    WebView mWebView;

    private String redirectUri;

    private boolean isGenoaPay = false;

    private EventListener eventListener = null;

    public LPayViewController(Bundle args) {
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

        redirectUri = Settings.getSelectedCountry().legacyRoot + "Checkout.aspx?cid=10";

        if (!mPresenter.isBusy()) {
            mPresenter.createLPayOrder(redirectUri);
            logCreateOrder();
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
        hideLPayWebView();
        hideProgressIndicator();
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_lpay, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void showLPayWebView(String paymentUrl) {
        if (mWebView.getUrl() == null || mWebView.getUrl().equals("about:blank")) {
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.setWebViewClient(getWebViewClientForInitialize());
            mWebView.loadUrl(paymentUrl);
        }

        mWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLPayWebView() {
        mWebView.setVisibility(View.GONE);
    }

    @Override
    public void showProgressIndicator() {
        showLoading(LoadingDialogType.LPAY);
    }

    @Override
    public void hideProgressIndicator() {
        hideLoading();
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
            mActivity.getCurrentRouter()
                    .pushController(RouterTransaction
                            .with(new PaymentSuccessController(bundleBuilder.build()))
                            .pushChangeHandler(new HorizontalChangeHandler())
                            .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    @Override
    public void showError(String message) {
        hideLPayWebView();
        hideProgressIndicator();

        if (message == null || message.isEmpty()) {
            showAlertDialog(mActivity.getResources()
                    .getString(R.string.lpay_failed_transaction));
        } else {
            showAlertDialog(message);
        }
    }

    private void showError() {
        showError(null);
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(mActivity)
                .setTitle(mActivity.getResources().getString(isGenoaPay ? R.string.genoapay : R.string.lpay))
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setOnDismissListener(dialog -> dismissSelf())
                .show();
    }

    private WebViewClient getWebViewClientForInitialize() {
        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // prevents a redirect loop and sets up to catch LPay redirect url
                if (url.contains("about:blank")) {
                    showError();
                    logError("LPay url pointed to about:blank");
                    return true;
                } else if (url.contains(redirectUri)) {
                    final Uri uri = Uri.parse(url);
                    final String result = uri.getQueryParameter("result");
                    String message = uri.getQueryParameter("message");
                    if (result != null && !result.equalsIgnoreCase("FAILED")) {
                        confirmTransaction(url);
                    } else {
                        if (message == null) {
                            showError();
                            logError("LPay failed, but gave out no message.");
                        } else {
                            showError(message);
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    private void confirmTransaction(String sourceUrl) {
        hideLPayWebView();
        showProgressIndicator();
        final Uri uri = Uri.parse(sourceUrl);
        final String token = uri.getQueryParameter("token");
        final String reference = uri.getQueryParameter("reference");
        final String signature = uri.getQueryParameter("signature");
        final String message = uri.getQueryParameter("message");
        mPresenter.confirmLPayTransaction(token, signature, reference);
        logCreateCharge();
    }

    private void dismissSelf() {
        mActivity.onBackPressed();
    }

    public boolean isBusy() {
        return mPresenter.isBusy();
    }

    private void logCreateOrder() {
        if (eventListener != null) {
            eventListener.onCreateOrder();
        }
    }

    private void logCreateCharge() {
        if (eventListener != null) {
            eventListener.onCreateCharge();
        }
    }

    private void logError(String errorMessage) {
        if (eventListener != null) {
            eventListener.onError(errorMessage);
        }
    }

    public boolean isGenoaPay() {
        return isGenoaPay;
    }

    public void setGenoaPay(boolean genoaPay) {
        isGenoaPay = genoaPay;
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public interface EventListener {
        void onCreateOrder();

        void onCreateCharge();

        void onError(String errorMessage);
    }
}
