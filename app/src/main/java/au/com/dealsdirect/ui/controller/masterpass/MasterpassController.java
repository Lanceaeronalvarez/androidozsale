package au.com.dealsdirect.ui.controller.masterpass;
/*
 * Created by CodeineBot on 8/3/17.
 */

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.NetworkUtils;
import butterknife.BindView;
import butterknife.OnClick;

public class MasterpassController extends BaseController implements MasterpassMvpView {

    public static final String TAG = "MasterpassController";

    @Inject
    MasterpassMvpPresenter<MasterpassMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mTitleTextView;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mFilterButton;

    @BindView(R.id.controller_masterpass_web)
    WebView mWebView;

    private String mBaseUrl = "";

    public static MasterpassController newInstance() {
        return new MasterpassController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public MasterpassController(Bundle args) {
        super(args);
    }


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_masterpass, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void setUp(View view) {
        mFilterButton.setVisibility(View.INVISIBLE);
        mTitleTextView.setText("Masterpass");

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                String url = request.getUrl().toString();

                if (!mBaseUrl.isEmpty() && url.contains(mBaseUrl)) {
                    AppLogger.d(TAG + " should load url: " + url);
					//view.loadUrl(url.replace("https", "http"));
                } else {

                    // Hide loading
                    hideLoadingDialog();

                    // parse
                    HashMap<String, String> params = (HashMap<String, String>) NetworkUtils.getQueryParams(url);
                    String oAuthToken = params.get("oauth_token");
                    String oAuthVerifier = params.get("oauth_verifier");
                    String checkoutResourceUrl = params.get("checkout_resource_url");

                    AppLogger.d(TAG + "parse oauth_verifier: " + oAuthVerifier);

                    if (oAuthToken != null
                            && oAuthVerifier != null
                            && checkoutResourceUrl != null
                            && !oAuthToken.isEmpty()
                            && !oAuthVerifier.isEmpty()
                            && !checkoutResourceUrl.isEmpty()) {

                        mWebView.stopLoading();
                        showLoadingDialog("Confirming Payment", false);
                        mPresenter.confirmPayment(oAuthToken, oAuthVerifier, checkoutResourceUrl);
                    } else {
                        // Assume logout
                        getActivity().onBackPressed();
                    }
                }

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                hideLoading();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                showLoadingDialog("Processing", false);
            }
        });

        showLoadingDialog("Confirming Payment", false);

        mPresenter.getMasterpassPayment();
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void loadMasterpassUrl(String url, String host) {
        if (url.isEmpty()) return;

        mBaseUrl = host;
        showLoadingDialog("Redirecting", false);
        mWebView.loadUrl(url);
    }

    @Override
    public void showError(String message) {
        CustomAlertDialog.showCustomAlertDialog(
                getActivity(),
                CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                message);

        getActivity().onBackPressed();
    }

    @Override
    public void showPaymentSuccess(String address, String price, String invoice, String delivery) {
        getRouter().pushController(RouterTransaction.with(PaymentSuccessController.newInstance(address, price, invoice, delivery))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    public void onClickBack() {
        getActivity().onBackPressed();
    }
}
