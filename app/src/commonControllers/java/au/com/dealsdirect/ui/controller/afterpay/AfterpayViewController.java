package au.com.dealsdirect.ui.controller.afterpay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.login.PopUpHostController;
import au.com.dealsdirect.ui.main.PaymentInfo;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.StringUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

public class AfterpayViewController extends BaseController implements AfterpayMvpView {

    @Inject
    AfterpayMvpPresenter<AfterpayMvpView> mPresenter;

    @BindView(R.id.controller_afterpay_webview)
    WebView mWebView;

    private String mInitiliazeToken;

    private String mOrderToken;

    public AfterpayViewController(Bundle args) {
        super(args);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        mActivity.getMainController().hideBottomNav();

        if (!mPresenter.isBusy()) {
            if (mOrderToken == null || mOrderToken.isEmpty()) {
                if (mInitiliazeToken == null || mInitiliazeToken.isEmpty()) {
                    mPresenter.createAfterpayOrder();
                } else {
                    showAfterpayWebView(mInitiliazeToken);
                }
            } else {
                mPresenter.payWithAfterpay(mOrderToken);
            }
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
        hideAfterpayWebView();
        hideProgressIndicator();
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_afterpay, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void showAfterpayWebView(String token) {
        if (mWebView.getUrl() == null || mWebView.getUrl().equals("about:blank")) {
            mInitiliazeToken = token;
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.setWebViewClient(getWebViewClientForInitialize(token));
            String source = StringUtils.loadAssetTextAsString(mActivity, "js_loader.html");
            assert source != null;
            source = source.replace("JS_ADDRESS_GOES_HERE", mPresenter.getAfterpayScriptUri());
            mWebView.loadDataWithBaseURL(null, source, "text/html", "UTF-8", null);
        }

        mWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAfterpayWebView() {
        mWebView.setVisibility(View.GONE);
    }

    @Override
    public void showProgressIndicator() {
        showAfterpayLoading();
    }

    @Override
    public void hideProgressIndicator() {
        hideAfterpayLoading();
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
            Router router = mActivity.getHomeController().getPopUpHostRouter();
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
        hideAfterpayWebView();
        hideProgressIndicator();

        if (message == null || message.isEmpty()) {
            showAlertDialog(mActivity.getResources()
                    .getString(R.string.afterpay_failed_transaction));
        } else {
            showAlertDialog(message);
        }
    }

    private void showError() {
        showError(null);
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(mActivity)
                .setTitle(mActivity.getResources().getString(R.string.afterpay))
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setOnDismissListener(dialog -> dismissSelf())
                .show();
    }

    private WebViewClient getWebViewClientForInitialize(String token) {
        return new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String scriptInit = String
                        .format(mActivity.getResources().getString(R.string.afterpay_javascript_initialize),
                                mPresenter.getCountryIso());
                String scriptRedirect = String
                        .format(mActivity.getResources().getString(R.string.afterpay_javascript_redirect), token);
                view.evaluateJavascript(scriptInit + scriptRedirect, value -> {
                });
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // prevents a redirect loop and sets up to catch Afterpay redirect url
                view.setWebViewClient(getWebViewClientForRedirectCatch());

                view.loadUrl(url);
                return true;
            }
        };
    }

    private WebViewClient getWebViewClientForRedirectCatch() {
        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.contains(mPresenter.getRedirectUrlPrefix())) {
                    return true;
                }


                Uri uri = Uri.parse(url);
                String status = uri.getQueryParameter("status");

                if (status != null) {
                    mOrderToken = uri.getQueryParameter("orderToken");
                    switch (status.toLowerCase()) {
                        case "success":
                            if (mOrderToken != null && !mOrderToken.isEmpty()) {
                                hideAfterpayWebView();
                                mPresenter.payWithAfterpay(mOrderToken);
                                break;
                            } else {
                                logError(url);
                                showError("Unexpected error");
                            }
                            break;
                        case "failure":
                            logError(mOrderToken == null ? "No order token" : mOrderToken);
                            showError();
                            break;
                        default:
                            dismissSelf();
                            break;

                    }
                }
                return true;
            }
        };
    }

    private void dismissSelf() {
        mActivity.onBackPressed();
    }

    public boolean isBusy() {
        return mPresenter.isBusy();
    }

    private void logError(String errorMessage) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.PAYMENT_METHOD_TYPE,
                PaymentInfo.TYPE_AFTERPAY);
        parameters.put(au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.IS_NEW_USER,
                mPresenter.getIsNewUser());
        parameters.put(au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.RESULT, false);
        parameters.put(au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.SCREEN_NAME,
                AfterpayViewController.class.getSimpleName());
        parameters.put(au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.FAILED_TRANSACTION_MESSAGE,
                errorMessage);
        au.com.dealsdirect.service.datacollection.core.DataCollector.logEvent(Events.FailedTransaction, parameters);
    }
}
