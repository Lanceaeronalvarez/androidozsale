package au.com.dealsdirect.ui.controller.country;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.country.Country;
import au.com.dealsdirect.ui.base.BasePresenter;
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

        Country australia = new Country();
        australia.setCountry("Australia");
        australia.setCurrency("AUD");
        australia.setShopCode("BA");
        australia.setShopName("BuyInvate");

        Country newzealand = new Country();
        newzealand.setCountry("New Zealand");
        newzealand.setCurrency("NZD");
        newzealand.setShopCode("BN");
        newzealand.setShopName("BuyInvatew");

        countries.add(0, australia);
        countries.add(1, newzealand);



        getMvpView().showCountries(countries, getDataManager().getCountryId());
        getMvpView().hideLoading();
    }

    @Override
    public void onCountryItemClick(Country country) {
        getMvpView().showSelectedCountryDialog(country.getCountry());
        getDataManager().setCountryId(country.getShopCode());
        getMvpView().onBackPress();
    }

}
