package au.com.dealsdirect.ui.controller.legalities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;

/**
 * Created by Paul on 7/14/17.
 */

public class LegalitiesController extends SwipeableBaseToolBarController implements LegalitiesMvpView {

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
        setupSwipingBehavior();
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
