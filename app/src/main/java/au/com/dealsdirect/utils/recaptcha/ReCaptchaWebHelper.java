package au.com.dealsdirect.utils.recaptcha;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.StringUtils;

public class ReCaptchaWebHelper {

    public static String JAVASCRIPT_INTERFACE = "Android";

    private WebView webView = null;
    private ViewGroup parentView = null;

    private String receivedToken = null;
    private OnPassTokenListener listener = null;

    public ReCaptchaWebHelper() {
    }

    public void initiateReCaptchaV2Challenge(ViewGroup parentView, String siteKey, OnPassTokenListener listener) {
        (new Handler(parentView.getContext().getMainLooper())).post(() -> initiateReCaptchaChallenge("js_recaptcha_v2_loader.html", parentView, siteKey, listener));
    }

    public void initiateReCaptchaV3Challenge(ViewGroup parentView, String siteKey, OnPassTokenListener listener) {
        (new Handler(parentView.getContext().getMainLooper())).post(() -> initiateReCaptchaChallenge("js_recaptcha_v3_loader.html", parentView, siteKey, listener));
    }

    public void onBackPressed() {
        finish();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initiateReCaptchaChallenge(String loaderSource, ViewGroup parentView, String siteKey, OnPassTokenListener listener) {
        this.listener = listener;
        webView = new WebView(parentView.getContext());
        parentView.addView(webView);
        this.parentView = parentView;
        webView.setVisibility(ViewGroup.VISIBLE);
        ViewGroup.LayoutParams layoutParams = webView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        webView.setLayoutParams(layoutParams);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new ReCaptchaJSInterface(this), JAVASCRIPT_INTERFACE);
        String source = StringUtils.loadAssetTextAsString(parentView.getContext(), loaderSource);
        assert source != null;
        source = source
                .replaceAll("RECAPTCHA_SITE_KEY", siteKey)
                .replace("CALLBACK_GOES_HERE",
                        "console.log(\"Passing token...\");\n" +
                                JAVASCRIPT_INTERFACE + ".passToken(token);\n" +
                                "console.log(\"Finishing...\");\n" +
                                JAVASCRIPT_INTERFACE + ".finish();");
        webView.loadDataWithBaseURL(Settings.getSelectedCountry().genieRoot, source, "text/html", "UTF-8", null);

        CommonUtils.fadeInView(webView, null);
    }

    private void finish() {
        if (parentView == null || webView == null) {
            return;
        }
        (new Handler(parentView.getContext().getMainLooper())).post(this::finishOnMainThread);
    }

    private void finishOnMainThread() {
        final ViewGroup parentView = this.parentView;
        final WebView webView = this.webView;
        CommonUtils.fadeOutView(webView, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                parentView.removeView(webView);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                parentView.removeView(webView);
            }
        });
        webView.removeJavascriptInterface(JAVASCRIPT_INTERFACE);
        this.webView = null;
        this.parentView = null;

        if (listener != null) {
            listener.onReceiveToken(receivedToken);
        }
    }

    private static class ReCaptchaJSInterface {
        ReCaptchaWebHelper reCaptchaWebHelper;

        public ReCaptchaJSInterface(ReCaptchaWebHelper reCaptchaWebHelper) {
            this.reCaptchaWebHelper = reCaptchaWebHelper;
        }

        @JavascriptInterface
        public void passToken(String token) {
            reCaptchaWebHelper.receivedToken = token;
        }

        @JavascriptInterface
        public void finish() {
            reCaptchaWebHelper.finish();
        }
    }

    public interface OnPassTokenListener {
        void onReceiveToken(String token);
    }
}
