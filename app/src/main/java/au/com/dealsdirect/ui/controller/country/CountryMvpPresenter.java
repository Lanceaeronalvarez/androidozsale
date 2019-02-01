package au.com.dealsdirect.ui.controller.country;

import au.com.dealsdirect.data.network.model.country.Country;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by Admin on 12/18/17.
 */

public interface CountryMvpPresenter <V extends MvpView> extends MvpPresenter<V> {
    void getUserCountries();

    void onCountryItemClick(Country country);

    void setCountry(Country country);

    boolean shouldShowStrictConsent();

    void setShowStrictConsent(boolean isShowStrictContent);
}
