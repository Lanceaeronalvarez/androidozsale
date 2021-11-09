package au.com.dealsdirect.ui.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import au.com.dealsdirect.R;
import au.com.dealsdirect.utils.StringUtils;

public class BottomSheetInfoWebViewDialog extends BottomSheetDialogFragment {

    private String webViewContent = "";
    private String buttonTitle = "";
    private OnButtonClickListener onButtonClickListener = null;
    private boolean dismissOnButtonClick = true;
    private OnDismissListener onDismissListener = null;
    private OnCancelListener onCancelListener = null;

    private int layoutId = R.layout.bottom_sheet_info_webview;

    private boolean closeButtonVisible = false;

    private ViewGroup container = null;
    private WebView webView = null;
    private ImageButton closeButton = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTransparentBackgroundTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(layoutId, container, false);

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
        });

        setupWebview();
        setupCloseButton();

        if (shouldHideButton()) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setText(buttonTitle);
            button.setOnClickListener(v1 -> {
                if (dismissOnButtonClick) {
                    dismiss();
                }
                if (onButtonClickListener != null) {
                    onButtonClickListener.onClick();
                }
            });
        }

        closeButton.setOnClickListener(v12 -> dismiss());

        return v;
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

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public void setupButton(String buttonTitle, OnButtonClickListener onButtonClickListener) {
        this.buttonTitle = buttonTitle;
        this.onButtonClickListener = onButtonClickListener;
    }

    public boolean isCloseButtonVisible() {
        return closeButtonVisible;
    }

    public void setCloseButtonVisible(boolean closeButtonVisible) {
        this.closeButtonVisible = closeButtonVisible;
        setupCloseButton();
    }

    private void setupCloseButton() {
        if (closeButton == null) {
            return;
        }

        closeButton.setVisibility(closeButtonVisible ? View.VISIBLE : View.GONE);
        if (container != null && container.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) container.getLayoutParams();
            final int topMargin = closeButtonVisible ? closeButton.getLayoutParams().height / 2 : 0;
            layoutParams.setMargins(0, topMargin, 0, 0);
        }
    }

    public Boolean isDismissOnButtonClick() {
        if (shouldHideButton()) {
            return null;
        } else {
            return dismissOnButtonClick;
        }
    }

    public void setDismissOnButtonClick(boolean dismissOnButtonClick) {
        this.dismissOnButtonClick = dismissOnButtonClick;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public OnCancelListener getOnCancelListener() {
        return onCancelListener;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    @Override
    public void dismiss() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
        super.dismiss();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        if (onCancelListener != null) {
            onCancelListener.onCancel();
        }
        super.onCancel(dialog);
    }

    public interface OnButtonClickListener {
        void onClick();
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    public interface OnCancelListener {
        void onCancel();
    }
}
