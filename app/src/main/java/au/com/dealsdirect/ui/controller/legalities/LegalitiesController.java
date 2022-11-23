package au.com.dealsdirect.ui.controller.legalities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Paul on 7/14/17.
 */

public class LegalitiesController extends BasePullToRefreshController implements LegalitiesMvpView {

    @Inject
    LegalitiesMvpPresenter<LegalitiesMvpView> mPresenter;

    @BindView(R.id.controller_legalities_base_webview)
    protected WebView mWebView;

    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mFilterButton;

    @BindView(R.id.partial_toolbar_title)
    TextView mTitleText;

    @Nullable
    @BindView(R.id.partial_toolbar_subtitle)
    TextView mSubtitleText;


    private String key;

    private int stringResource;

    private String title;
    private String subtitle;

    public LegalitiesController(String key, String title) {
        this(new BundleBuilder(new Bundle())
                .putString(BundleKeys.TEMPLATE_KEY, key)
                .putString(BundleKeys.LEGALITIES_TITLE, title)
                .build());
    }

    public LegalitiesController(int stringResource, String title) {
        this(new BundleBuilder(new Bundle())
                .putInt(BundleKeys.STRING_RESOURCE, stringResource)
                .putString(BundleKeys.LEGALITIES_TITLE, title)
                .build());
    }

    public LegalitiesController(Bundle args) {
        super(args);
        key = args.getString(BundleKeys.TEMPLATE_KEY, null);
        stringResource = args.getInt(BundleKeys.STRING_RESOURCE, -1);
        title = args.getString(BundleKeys.LEGALITIES_TITLE);
        if (title != null && title.contains("\n")) {
            String[] titles = title.split("\n");
            title = titles[0];
            subtitle = titles[1];
        } else {
            subtitle = null;
        }
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        ToolBarType toolBarType = subtitle != null ? ToolBarType.TWOLINES : ToolBarType.ARROW;
        View view = super.inflateView(inflater, container, toolBarType);

        setToolBarVisible(getResource().getBoolean(R.bool.legalities_toolbar_visibility));
        fillContent(inflater.inflate(R.layout.controller_legalities, container, false));

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mWebView.setVisibility(View.GONE);
        loadContent();
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mTitleText.setText(title);
        if (mSubtitleText != null) {
            mSubtitleText.setText(subtitle);
        }
        mFilterButton.setVisibility(View.INVISIBLE);
        loadContent();

        mWebView.getSettings().setTextZoom(100);
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }

    @Override
    public void displayFetchedText(String value) {
        String header = mActivity.getResources().getString(R.string.base_html_template_header);
        String footer = mActivity.getResources().getString(R.string.base_html_template_footer);

        mWebView.loadDataWithBaseURL(null, header + value + footer,
                "text/html", "UTF-8", null);
        mWebView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void backPress() {
        mActivity.onBackPressed();
    }

    private void loadContent() {
        if (key != null) {
            mPresenter.loadText(key);
        } else if (stringResource >= 0) {
            String appname = mActivity.getResources().getString(R.string.app_name);
            String text = mActivity.getResources().getString(stringResource)
                    .replaceAll("\\{appname\\}", appname)
                    .replaceAll("\\{APPNAME\\}", appname.toUpperCase());
            displayFetchedText(text);
        }
    }
}
