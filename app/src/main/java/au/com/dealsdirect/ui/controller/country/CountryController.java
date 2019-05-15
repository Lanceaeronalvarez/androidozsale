package au.com.dealsdirect.ui.controller.country;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.country.Country;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Admin on 12/18/17.
 */

public class CountryController extends BasePullToRefreshController implements CountryMvpView {

    public static final String TAG = "CountryController";
    private static final String BUNDLE_CALLED_AFTER_SPLASH = "BUNDLE_CALLED_AFTER_SPLASH";

    @Inject
    CountryMvpPresenter<CountryMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_title)
    TextView mTitleText;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mFilterView;

    @BindView(R.id.partial_toolbar_left_view)
    View mArrowImage;

    @BindView(R.id.controller_recycler_details)
    RecyclerView mRecyclerView;

    private CountryAdapter mAdapter;
    private boolean mIsAfterSplash;
    private boolean mHasSavedInstance;
    private boolean shouldShowStrictConsent;
    private String previousSelectedCountry = "";

    public static CountryController newInstance() {
        return new CountryController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public CountryController(boolean calledAfterSplash) {
        this(new BundleBuilder(new Bundle())
                .putBoolean(BUNDLE_CALLED_AFTER_SPLASH, calledAfterSplash)
                .build());
    }

    public CountryController(Bundle args) {
        super(args);
        mIsAfterSplash = args.getBoolean(BUNDLE_CALLED_AFTER_SPLASH, false);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
        outState.putBoolean(BUNDLE_CALLED_AFTER_SPLASH, mIsAfterSplash);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
        mIsAfterSplash = savedInstanceState.getBoolean(BUNDLE_CALLED_AFTER_SPLASH);

    }


    @Override
    public void showCountries(List<Country> countries, String selectedCountry) {
        mAdapter.replaceData(countries, selectedCountry);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mPresenter.getUserCountries();
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showSelectedCountryDialog(Country country) {
        Settings.Country selectedCountry = Settings.getCountryWithId(country.getShopCode());
        mActivity.setAppCountries(selectedCountry);
        mPresenter.setCountry(country);

        CustomAlertDialog.showCustomAlertDialog(mActivity,
                CustomAlertDialog.CustomDialogIconState.POSITIVE,
                Settings.getSelectedCountry().countryName);

        String[] array = mActivity.getResources().getStringArray(R.array.gdpr_countries);
        List<String> mGdprCountriesArray = new ArrayList<String>(Arrays.asList(array));

        if (mGdprCountriesArray.contains(country.getCountry().toLowerCase()) && mPresenter.shouldShowStrictConsent()
                && !previousSelectedCountry.equalsIgnoreCase(country.getShopCode())) {
            mActivity.callAppConsent();
        } else {
            mActivity.initializeMainController();
        }
    }

    @Override
    protected void setUp(View view) {
        mTitleText.setText("Country");
        mFilterView.setVisibility(View.INVISIBLE);

        if (mIsAfterSplash){
            mArrowImage.setVisibility(View.INVISIBLE);
        }
        previousSelectedCountry = mPresenter.getCurrentSelectedCountry();
        mAdapter = new CountryAdapter(previousSelectedCountry, new ArrayList<>(), mActivity, mPresenter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.getUserCountries();
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        mActivity.onBackPressed();
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container, ToolBarType.ARROW);

        setToolBarVisible(getResource().getBoolean(R.bool.countries_toolbar_visibility));
        fillContent(inflater.inflate(R.layout.controller_user_countries, container, false));

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

}
