package au.com.dealsdirect.ui.controller.legalities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.base.BaseToolBarController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Paul on 7/14/17.
 */

public class LegalitiesController extends BaseToolBarController implements LegalitiesMvpView {

    @Inject
    LegalitiesMvpPresenter<LegalitiesMvpView> mPresenter;

    @BindView(R.id.controller_legalities_base_webview)
    protected WebView mWebView;

    String ourpayTermsAndConditionKey = "OurPayTermsAndConditions_Text";

    private String key;

    private String title;

    public LegalitiesController(Bundle args) {
        super(args);
        key = args.getString(BundleKeys.TEMPLATE_KEY);
        title = args.getString(BundleKeys.TITLE);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_legalities, container, false));

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mWebView.setVisibility(View.GONE);
        mPresenter.loadText(key);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mActivity.setDraggableViewPager(false);

        mToolbarTitle.setText(title);
        mPresenter.loadText(key);

    }

    @Override
    public void displayFetchedText(String value) {
        String header = mActivity.getResources().getString(R.string.base_html_template_header);
        String footer = mActivity.getResources().getString(R.string.base_html_template_footer);

        mWebView.loadData(header + value + footer, "text/html; charset=UTF-8", null);
        mWebView.setVisibility(View.VISIBLE);
    }
}
