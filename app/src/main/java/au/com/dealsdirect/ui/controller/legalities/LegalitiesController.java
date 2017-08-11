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
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Paul on 7/14/17.
 */

public class LegalitiesController extends BaseController implements LegalitiesMvpView {

    @Inject
    LegalitiesMvpPresenter<LegalitiesMvpView> mPresenter;

    @BindView(R.id.controller_legalities_base_webview)
    protected WebView mWebView;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageButton mFilterButton;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mTitleText;

    String ourpayTermsAndConditionKey = "OurPayTermsAndConditions_Text";

    private String key;

    private String title;

    public static final String TEMPLATE_KEY = "TEMPLATE_KEY";

    public static final String TITLE = "TITLE";

    public LegalitiesController(String  key, String title) {
        this(new BundleBuilder(new Bundle())
                .putString(TEMPLATE_KEY,key)
                .putString(TITLE, title)
                .build());
    }

    public LegalitiesController(Bundle args) {
        super(args);
        key = args.getString(TEMPLATE_KEY);
        title = args.getString(TITLE);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_legalities, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        ((MainActivity)getActivity()).setDraggableViewPager(false);

        mTitleText.setText(title);
        mFilterButton.setVisibility(View.INVISIBLE);

        if (key.equals(ourpayTermsAndConditionKey)){
            displayFetchedText(((MainActivity)getActivity()).getMyTemplateTexts(key));

        }else{
            mPresenter.loadText(key);
        }
    }

    @Override
    public void displayFetchedText(String value) {
        String header = getActivity().getResources().getString(R.string.base_html_template_header);
        String footer = getActivity().getResources().getString(R.string.base_html_template_footer);

        mWebView.loadData(header + value + footer, "text/html; charset=UTF-8", null);
    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    public void backPress() {
        getActivity().onBackPressed();
    }
}
