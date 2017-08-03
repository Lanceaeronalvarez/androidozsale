package au.com.dealsdirect.ui.controller.masterpass;
/*
 * Created by CodeineBot on 8/3/17.
 */

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.NetworkUtils;
import butterknife.BindView;

public class MasterpassController extends BaseController implements MasterpassMvpView {

    public static final String TAG = "MasterpassController";

    @Inject
    MasterpassMvpPresenter<MasterpassMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_title_view)
    TextView mTitleTextView;

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
                    hideLoading();

                    // parse
                    HashMap<String, String> params = (HashMap<String, String>) NetworkUtils.getQueryParams(url);
                    String oAuthToken = params.get("oauth_token");
                    String oAuthVerifyer = params.get("oauth_verifier");
                    String checkoutResourceUrl = params.get("checkout_resource_url");

                    Log.d("Masterpass", "parse oauth_verifier: " + oAuthVerifyer);

                    if (oAuthToken != null
                            && oAuthVerifyer != null
                            && checkoutResourceUrl != null
                            && !oAuthToken.isEmpty()
                            && !oAuthVerifyer.isEmpty()
                            && !checkoutResourceUrl.isEmpty()) {
//                        confirmPayment(oAuthToken, oAuthVerifyer, checkoutResourceUrl);
                    } else {
                        // Assume logout
                        getActivity().onBackPressed();
                        //OEngine.backToCart(getBaseActivity());
                    }
                }

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

//                CookieSyncManager.getInstance().sync();

                hideLoading();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

//                hideProgressHud();
//
//                showProgressHud(getBaseActivity(),
//                        getBaseActivity().getResources().getString(R.string.processing), true, false, null);
            }
        });



    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }
}
