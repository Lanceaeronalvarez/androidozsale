package au.com.dealsdirect.ui.controller.gdpr;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

public class StrictConsentController extends BaseController {

    public static final String TAG = StrictConsentController.class.getSimpleName();

    private static final String JS_INTERFACE_TAG = "app";

    private static final String WEBVIEW_URL_EXTENSION = "///";

    private static final String ASSET_FOLDER = "file:android_asset/";

    private static final String CONSENT_HTML_LOCATION = ASSET_FOLDER + "index.html";

    private static final String PRIVACY_POLICY_HTML_LOCATION = ASSET_FOLDER + "personal.html";

    @Inject
    StrictConsentMvpPresenter<StrictConsentMvpView> mPresenter;

    @BindView(R.id.controller_strict_consent_web_view)
    WebView mWebView;

    @BindView(R.id.controller_strict_consent_button)
    Button mButton;

    @BindView(R.id.partial_toolbar_field_title_left_option)
    View mBackButton;

    public StrictConsentController(Bundle args) {
        super(args);
    }

    @Override
    public boolean handleBack() {
        if (mWebView != null &&
                mWebView.getUrl() != null &&
                mWebView.getUrl().replace(WEBVIEW_URL_EXTENSION, "")
                .equals(PRIVACY_POLICY_HTML_LOCATION)) {
            mWebView.loadUrl(CONSENT_HTML_LOCATION);
            return true;
        } else {
            return super.handleBack();
        }
    }

    public static StrictConsentController newInstance() {
        return new StrictConsentController(new BundleBuilder(new Bundle()).build());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_strict_consent, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.addJavascriptInterface(new WebViewJavascriptInterface(mActivity), JS_INTERFACE_TAG);
        mWebView.loadUrl(CONSENT_HTML_LOCATION);
        mBackButton.setVisibility(View.INVISIBLE);

        mButton.setText(mPresenter.getConsentContinueText());
        mButton.setOnClickListener(v -> mActivity.onClickAgreeStrictConsentUI());
    }

    public class WebViewJavascriptInterface {

        private Context context;

        public WebViewJavascriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void loadPrivacyPolicy() {
            mWebView.post(() -> {
                mBackButton.setVisibility(View.VISIBLE);
                mWebView.loadDataWithBaseURL(
                        null,
                        mPresenter.getConsentFullText()
                                + "<br><br><br><br><br><br><br>",
                        "text/html", "UTF-8", null);

            });
        }
    }

    @OnClick(R.id.partial_toolbar_field_title_left_option)
    public void onClick() {
        mWebView.loadUrl(CONSENT_HTML_LOCATION);
        mBackButton.setVisibility(View.INVISIBLE);
    }

}
