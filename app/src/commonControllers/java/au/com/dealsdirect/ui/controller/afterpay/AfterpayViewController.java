package au.com.dealsdirect.ui.controller.afterpay;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import au.com.dealsdirect.utils.StringUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

public class AfterpayViewController extends BaseController implements AfterpayMvpView {

    @Inject
    AfterpayMvpPresenter<AfterpayMvpView> mPresenter;

    @BindView(R.id.controller_afterpay_toolbar_container)
    RelativeLayout mToolbarContainer;

    @BindView(R.id.partial_toolbar_title)
    TextView mTitleView;

    @BindView(R.id.partial_toolbar_left_view)
    TextView mBackButton;

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
        mTitleView.setText(mActivity.getResources().getString(R.string.afterpay));

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
        mToolbarContainer.setVisibility(View.GONE);
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
        mToolbarContainer.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
    }

    @Override
    public void showProgressIndicator() {
        mBackButton.setEnabled(false);
        mBackButton.setVisibility(View.INVISIBLE);
        showOurpayLoading();
    }

    @Override
    public void hideProgressIndicator() {
        mBackButton.setEnabled(true);
        mBackButton.setVisibility(View.INVISIBLE);
        hideOurpayLoading();
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
        onError(message);
        dismissSelf();
    }

    @OnClick({R.id.partial_toolbar_left_view})
    void onCloseClick() {
        mActivity.onBackPressed();
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
                Uri uri = Uri.parse(url);
                String status = uri.getQueryParameter("status");

                if (status != null) {
                    switch (status.toLowerCase()) {
                        case "success":
                            mOrderToken = uri.getQueryParameter("orderToken");
                            if (mOrderToken != null && !mOrderToken.isEmpty()) {
                                hideAfterpayWebView();
                                mPresenter.payWithAfterpay(mOrderToken);
                                break;
                            }
                            // waterfall on null orderToken
                        default:
                            dismissSelf();
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
}
