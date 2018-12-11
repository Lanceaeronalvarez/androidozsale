package au.com.dealsdirect.ui.controller.country;

import java.util.List;

import au.com.dealsdirect.data.network.model.country.Country;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by Admin on 12/18/17.dp
 */

public interface CountryMvpView extends MvpView {
    void showCountries(List<Country> countries, String selectedCountry);

    void showSelectedCountryDialog(Country Country);

}
