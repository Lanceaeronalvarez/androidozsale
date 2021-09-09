package au.com.dealsdirect.ui.controller.webviewcontroller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;
import butterknife.OnClick;

public class WebViewController extends BasePullToRefreshController implements WebViewMvpView {

    @Inject
    WebViewMvpPresenter<WebViewMvpView> mPresenter;

    @BindView(R.id.controller_webview_webview)
    protected WebView mWebView;

    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mFilterButton;

    @BindView(R.id.partial_toolbar_title)
    TextView mTitleText;

    private boolean isUrlJavascript;

    private String url;

    private String title;

    private WebViewClient mWebViewClient;

    public WebViewController(Bundle args) {
        super(args);
        title = args.getString(BundleKeys.KEY_WEBVIEW_CONTROLLER_TITLE);
        url = args.getString(BundleKeys.KEY_WEBVIEW_CONTROLLER_URL);
        isUrlJavascript = args.getBoolean(BundleKeys.KEY_WEBVIEW_CONTROLLER_IS_JS, false);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container, ToolBarType.ARROW);

        setToolBarVisible(getResource().getBoolean(R.bool.webview_toolbar_visibility));
        fillContent(inflater.inflate(R.layout.controller_webview, container, false));

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mWebView.setVisibility(View.GONE);
        mPresenter.loadFromUrl(url);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mTitleText.setText(title);
        mFilterButton.setVisibility(View.INVISIBLE);
        mPresenter.loadFromUrl(url);

    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void backPress() {
        mActivity.onBackPressed();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void showWebpage(String url) {
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setJavaScriptEnabled(isUrlJavascript);
        mWebView.setWebChromeClient(new WebChromeClient());
        if (isUrlJavascript) {
            String source = StringUtils.loadAssetTextAsString(mActivity, "js_loader.html");
            if (source == null) {
                return;
            }
            source = source.replace("JS_ADDRESS_GOES_HERE", url);
            mWebView.loadDataWithBaseURL(null, source, "text/html", "UTF-8", null);
        } else {
            mWebView.loadUrl(url);
        }
        mWebView.setVisibility(View.VISIBLE);
    }

    public WebViewClient getWebViewClient() {
        return mWebViewClient;
    }

    public void setWebViewClient(WebViewClient webViewClient) {
        mWebViewClient = webViewClient;
        if (mWebView != null && mWebView.isAttachedToWindow()) {
            mWebView.setWebViewClient(webViewClient);
        }
    }
}
