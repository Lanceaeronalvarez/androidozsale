package au.com.dealsdirect.ui.controller.country;

import com.mysale.genie.utility.Prefs;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.BuildConfig;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.country.Country;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.utils.CookieUtils;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Admin on 12/18/17.
 */

public class CountryPresenter<V extends CountryMvpView> extends BasePresenter<V> implements CountryMvpPresenter<V> {

    @Inject
    public CountryPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void getUserCountries() {

        List<Country> countries = new LinkedList<>();

        for (Settings.Country country : Settings.getSupportedCountries()) {
            Country listOfCountry = new Country();
            listOfCountry.setShopCode(country.countryId);
            listOfCountry.setShopName(BuildConfig.APP_NAME);
            listOfCountry.setCountry(country.countryName);
            countries.add(listOfCountry);
        }

        Collections.sort(countries, new Comparator<Country>() {
            @Override
            public int compare(Country s1, Country s2) {
                return s1.getCountry().compareToIgnoreCase(s2.getCountry());
            }
        });

        getMvpView().showCountries(countries, getDataManager().getCountryId());
        getMvpView().hideLoading();
    }

    @Override
    public void onCountryItemClick(Country country) {

        Prefs.clear();
        CookieUtils.getInstance().clear();

        getMvpView().showSelectedCountryDialog(country);

        Boolean isMultiCountry = getDataManager().getIsMultiCountry();
        getDataManager().setCountryId(Settings.getSelectedCountry().countryId);
        getDataManager().setIsMultiCountry(isMultiCountry);
        getDataManager().setLanguageId(Settings.getSelectedCountry().languageId);

    }

    @Override
    public void setCountry(Country country) {
        getDataManager().setCountryId(Settings.getSelectedCountry().countryId);
        getDataManager().setLanguageId(Settings.getSelectedCountry().languageId);
        getDataManager().setUserAgent();
    }

    @Override
    public boolean shouldShowStrictConsent() {
        return getDataManager().shouldShowStrictConsent();
    }

    @Override
    public void setShowStrictConsent(boolean isShowStrictContent) {
        getDataManager().setShouldShowStrictConsent(isShowStrictContent);
    }

    @Override
    public String getCurrentSelectedCountry() {
        return getDataManager().getCountryId();
    }


}
