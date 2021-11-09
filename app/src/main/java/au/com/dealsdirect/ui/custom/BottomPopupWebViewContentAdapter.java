package au.com.dealsdirect.ui.custom;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;

import au.com.dealsdirect.R;
import au.com.dealsdirect.utils.StringUtils;

public class BottomPopupWebViewContentAdapter implements BottomPopupView.BottomPopupViewAdapter {

    private String webViewContent = "";
    private String buttonTitle = "";
    private OnButtonClickListener onButtonClickListener = null;
    private OnCloseButtonClickListener onCloseButtonClickListener = null;

    private ViewGroup container = null;
    private WebView webView = null;
    private ImageButton closeButton = null;

    private View contentView = null;

    private WebViewClientOverrideUrlLoading webViewClientOverrideUrlLoading = null;

    public BottomPopupWebViewContentAdapter() {

    }

    @Override
    public View onCreate(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottom_sheet_info_webview, parent, false);

        container = v.findViewById(R.id.bottom_sheet_info_container);
        webView = v.findViewById(R.id.bottom_sheet_info_content);
        final Button button = v.findViewById(R.id.bottom_sheet_info_button);
        closeButton = v.findViewById(R.id.bottom_sheet_info_close_button);

        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (webViewClientOverrideUrlLoading != null) {
                    return webViewClientOverrideUrlLoading.shouldOverride(url);
                } else {
                    return url.contains("about:blank");
                }
            }
        });

        setupWebview();
        setupCloseButton();

        if (shouldHideButton()) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setText(buttonTitle);
            button.setOnClickListener(v1 -> {
                if (onButtonClickListener != null) {
                    onButtonClickListener.onClick();
                }
            });
        }

        closeButton.setOnClickListener(v12 -> {
            if (onCloseButtonClickListener != null) {
                onCloseButtonClickListener.onClick();
            }
        });

        contentView = v;
        return v;
    }

    public View getContentView() {
        return contentView;
    }

    private boolean shouldHideButton() {
        return buttonTitle == null || onButtonClickListener == null;
    }

    public String getWebViewContent() {
        return webViewContent;
    }

    public void setWebViewContent(String webViewContent) {
        this.webViewContent = webViewContent;
        setupWebview();
    }

    public void setupWebview() {
        if (webView == null) {
            return;
        }

        final Context context = webView.getContext();

        final String mHtmlHeader = StringUtils.applyStyleToCSS(new StringUtils.CSSStyle() {
            @Override
            public String getBodyFontName() {
                return StringUtils.typeFaceFamilyFromFilename(
                        context.getResources().getString(R.string.font_app_regular));
            }

            @Override
            public String getBodyFontColor() {
                String hex = Integer.toHexString(
                        context.getResources().getColor(R.color.text_extra_dark));
                if (hex.length() > 6) {
                    hex = hex.substring(2);
                }
                return "#" + hex;
            }

            @Override
            public String getBoldFontName() {
                return StringUtils.typeFaceFamilyFromFilename(
                        context.getResources().getString(R.string.font_app_regular));
            }

            @Override
            public String getBoldFontColor() {
                String hex = Integer.toHexString(
                        context.getResources().getColor(R.color.text_extra_dark));
                if (hex.length() > 6) {
                    hex = hex.substring(2);
                }
                return "#" + hex;
            }
        }, context.getResources()
                .getString(R.string.base_html_template_header));

        final String mHtmlFooter = context.getResources()
                .getString(R.string.base_html_template_footer);

        webView.loadDataWithBaseURL(null, mHtmlHeader + getWebViewContent() + mHtmlFooter,
                "text/html", "UTF-8", null);
    }

    public void setupButton(String buttonTitle, OnButtonClickListener onButtonClickListener) {
        this.buttonTitle = buttonTitle;
        this.onButtonClickListener = onButtonClickListener;
    }

    public boolean isCloseButtonVisible() {
        return onCloseButtonClickListener != null;
    }

    public void setOnCloseButtonClickListener(OnCloseButtonClickListener onCloseButtonClickListener) {
        this.onCloseButtonClickListener = onCloseButtonClickListener;
        setupCloseButton();
    }

    private void setupCloseButton() {
        if (closeButton == null) {
            return;
        }

        final boolean closeButtonVisible = onCloseButtonClickListener != null;

        closeButton.setVisibility(closeButtonVisible ? View.VISIBLE : View.GONE);
        if (container != null && container.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) container.getLayoutParams();
            final int topMargin = closeButtonVisible ? closeButton.getLayoutParams().height / 2 : 0;
            layoutParams.setMargins(0, topMargin, 0, 0);
        }
    }

    public void setWebViewClientOverrideUrlLoading(WebViewClientOverrideUrlLoading webViewClientOverrideUrlLoading) {
        this.webViewClientOverrideUrlLoading = webViewClientOverrideUrlLoading;
    }

    public interface OnButtonClickListener {
        void onClick();
    }

    public interface OnCloseButtonClickListener {
        void onClick();
    }

    public interface WebViewClientOverrideUrlLoading {
        boolean shouldOverride(String url);
    }
}
