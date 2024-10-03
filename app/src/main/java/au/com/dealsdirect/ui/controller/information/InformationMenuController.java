package au.com.dealsdirect.ui.controller.information;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.events.FeatureUsageEventRequest;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.EventTypeId;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.datacollection.enums.FeatureUsageEventType;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.account.model.AccountOption;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smartwave on 21/06/2018.
 */

public class InformationMenuController extends BaseController implements InformationMenuMvpView {

    @Inject
    InformationMenuMvpPresenter<InformationMenuMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_left_view)
    View mToolbarTextViewLeft;
    @BindView(R.id.partial_toolbar_title)
    TextView mTitleTextView;
    @BindView(R.id.controller_information_about_us)
    ViewGroup mAboutUsContainer;
    @BindView(R.id.controller_information_terms_and_conditions)
    ViewGroup mTermsAndConditionsContainer;
    @BindView(R.id.controller_information_privacy_policy)
    ViewGroup mPrivacyPolicyContainer;
    @BindView(R.id.controller_information_gc_tnc)
    ViewGroup mGiftCardTermsAndConditionsContainer;

    public static InformationMenuController newInstance() {
        return new InformationMenuController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public InformationMenuController(Bundle args) {
        super(args);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_information, container, false);
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
        mTitleTextView.setText(getString(R.string.account_information));
        mToolbarTextViewLeft.setVisibility(mPresenter.isTablet() ? View.GONE : View.VISIBLE);

        mAboutUsContainer.setOnClickListener(v -> showLegalities(BundleKeys.TEMPLATE_KEY_ABOUT_US, AccountOption.ABOUTUS));
        mTermsAndConditionsContainer.setOnClickListener(v -> showLegalities(BundleKeys.TEMPLATE_KEY_TNC, AccountOption.TERMSANDCONDITIONS));
        mPrivacyPolicyContainer.setOnClickListener(v -> showLegalities(BundleKeys.TEMPLATE_KEY_PRIVACY, AccountOption.PRIVACYPOLICY));
        if (mActivity.getResources().getBoolean(R.bool.is_gift_card_terms_and_conditions_visible)) {
            mGiftCardTermsAndConditionsContainer.setVisibility(View.VISIBLE);
            mGiftCardTermsAndConditionsContainer.setOnClickListener(v -> showLegalities(R.string.gc_tnc_content, AccountOption.GCTERMSANDCONDITIONS));
        } else {
            mGiftCardTermsAndConditionsContainer.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.partial_toolbar_left_view)
    void onBackPressed() {
        mActivity.onBackPressed();
    }

    private void showLegalities(String key, AccountOption option) {
        Bundle bundle = new BundleBuilder(new Bundle())
                .putString(BundleKeys.TEMPLATE_KEY, key)
                .putString(BundleKeys.LEGALITIES_TITLE, mActivity.getResources().getString(option.getTitleResourceId()))
                .build();
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.LEGALITIES,
                bundle,
                new HorizontalChangeHandler(false),
                new HorizontalChangeHandler());

        logNavigation(option);
    }

    private void showLegalities(int stringResource, AccountOption option) {
        Bundle bundle = new BundleBuilder(new Bundle())
                .putInt(BundleKeys.STRING_RESOURCE, stringResource)
                .putString(BundleKeys.LEGALITIES_TITLE, mActivity.getResources().getString(option.getTitleResourceId()))
                .build();
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.LEGALITIES,
                bundle,
                new HorizontalChangeHandler(false),
                new HorizontalChangeHandler());

        logNavigation(option);
    }

    private void logNavigation(AccountOption option) {
        switch (option) {
            case ABOUTUS:
                logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.ABOUT_US);
                break;
            case TERMSANDCONDITIONS:
                logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.TERMS_AND_CONDITIONS);
                break;
            case PRIVACYPOLICY:
                logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.PRIVACY_POLICY);
                break;
            case GCTERMSANDCONDITIONS:
                logMenuSelectFeatureUsageEvent(FeatureUsageEventType.GiftCards.TERMS_AND_CONDITIONS);
                break;
            default:
                break;
        }
    }

    private void logMenuSelectFeatureUsageEvent(int featureUsageEventType) {
        FeatureUsageEventRequest featureUsageEventRequest = new FeatureUsageEventRequest();
        featureUsageEventRequest.setEventType(EventTypeId.EVENT_FEATURE_USAGE);
        featureUsageEventRequest.setFeatureInfo(new FeatureUsageEventRequest.FeatureInfo(featureUsageEventType));

        HashMap<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, AccountController.class.getSimpleName());
        eventParameters.put(DataCollector.EventParameters.FEATURE_EVENT_REQUEST, featureUsageEventRequest);

        DataCollector.logEvent(Events.FeatureUsageEvent, eventParameters);
    }
}
