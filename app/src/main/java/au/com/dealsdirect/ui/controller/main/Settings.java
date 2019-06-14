package au.com.dealsdirect.ui.controller.main;

import android.util.Log;

import java.util.HashMap;
import au.com.dealsdirect.BuildConfig;

/**
 * Created by MTC on 9/4/18.
 */



public class Settings {

    public static class Country{
        public String countryName;
        public String countryId;
        public String accountId;
        public String currencySign;
        public String siteName;
        public String languageId;
        public String genieRoot;
        public String legacyRoot;

        Country(String countryName, String countryId, String accountId, String currencySign, String siteName,
                String languageId, String genieRoot, String legacyRoot) {
            this.countryName = countryName;
            this.countryId = countryId;
            this.accountId = accountId;
            this.currencySign = currencySign;
            this.siteName = siteName;
            this.languageId = languageId;
            this.genieRoot = genieRoot;
            this.legacyRoot = legacyRoot;
        }
    }

    private static Country selectedCountry;

    private static Country[] supportedCountries;

    private static String reCaptchaSiteKey;

    public static Country getSelectedCountry() {
        return selectedCountry;
    }

    public static void setCountry(Country newCountry) {
        selectedCountry = newCountry;
    }

    public static boolean getIsMultiCountry() {
        return supportedCountries.length > 1;
    }

    public static String getReCaptchaSiteKey() {
        return reCaptchaSiteKey;
    }

    public static void setReCaptchaSiteKey(String reCaptchaSiteKey) {
        Settings.reCaptchaSiteKey = reCaptchaSiteKey;
    }

    public static Country[] getSupportedCountries() {

        if (supportedCountries == null) {
            load();
        }

        return supportedCountries;
    }

    public static Country getCountryWithId(String identifier) {
        for (Country country : supportedCountries) {
            if (country.countryId.equals(identifier)) {
                return country;
            }
        }
        return null;
    }

    public static Country getDefaultCountry() {
        return !getIsMultiCountry() ? supportedCountries[0] : null;
    }

    static void load(){
        if (BuildConfig.FLAVOR.equals("cocosaRC")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("United Kingdom",
                                    "CU",
                                    "E9C291A6-80E1-4CD3-AF3E-4107BEBC376A",
                                    "£",
                                    "cocosa.co.uk",
                                    "EN",
                                    "https://www.cocosa.co.uk/",
                                    "https://www.cocosa.co.uk/"),
                            new Country("Australia",
                                    "CA",
                                    "954775B6-28C9-4C5C-880C-88F195C8E4F0",
                                    "$",
                                    "cocosa.com.au",
                                    "EN",
                                    "https://www.cocosa.com.au/",
                                    "https://www.cocosa.com.au/"),
                            new Country("New Zealand",
                                    "CN",
                                    "2B2D335D-0365-4D56-97B0-C6CE7F3AE2C7",
                                    "NZ$",
                                    "cocosa.co.nz",
                                    "EN",
                                    "https://www.cocosa.co.nz/",
                                    "https://www.cocosa.co.nz/")});
        } else if (BuildConfig.FLAVOR.equals("cocosaTest"))  {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("United Kingdom",
                                    "CU",
                                    "E9C291A6-80E1-4CD3-AF3E-4107BEBC376A",
                                    "£",
                                    "cocosa.co.uk",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/"),
                            new Country("Australia",
                                    "CA",
                                    "954775B6-28C9-4C5C-880C-88F195C8E4F0",
                                    "$",
                                    "cocosa.com.au",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/"),
                            new Country("New Zealand",
                                    "CN",
                                    "2B2D335D-0365-4D56-97B0-C6CE7F3AE2C7",
                                    "NZ$",
                                    "cocosa.co.nz",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/")
                    });
        } else if (BuildConfig.FLAVOR.equals("ooRC")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Australia",
                                        "OA",
                                        "25D201EB-9A1C-4045-90B6-E6A9F122C7CB",
                                        "$",
                                        "oo.com.au",
                                        "EN",
                                        "https://www.oo.com.au/",
                                        "https://www.oo.com.au/")} );

        } else if (BuildConfig.FLAVOR.equals("ooTest")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Australia",
                                    "OA",
                                    "25D201EB-9A1C-4045-90B6-E6A9F122C7CB",
                                    "$",
                                    "oo.com.au",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/")} );

        } else if (BuildConfig.FLAVOR.equals("buyinviteRC")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Australia",
                                        "BA",
                                        "FD6E7F98-F8B4-49D9-8FEF-D1AA02BCB43A",
                                        "$",
                                        "buyinvite.com.au",
                                        "EN",
                                        "https://www.buyinvite.com.au/",
                                        "https://www.buyinvite.com.au/"),

                            new Country("New Zealand",
                                        "BN",
                                        "461308FD-59C6-4BE1-A3D0-FCA74BCD338F",
                                        "NZ$",
                                        "buyinvite.co.nz",
                                        "EN",
                                        "https://www.buyinvite.co.nz/",
                                        "https://www.buyinvite.co.nz/")} );

        } else if (BuildConfig.FLAVOR.equals("buyinviteTest")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Australia",
                                    "BA",
                                    "FD6E7F98-F8B4-49D9-8FEF-D1AA02BCB43A",
                                    "$",
                                    "buyinvite.com.au",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/"),

                            new Country("New Zealand",
                                    "BN",
                                    "461308FD-59C6-4BE1-A3D0-FCA74BCD338F",
                                    "NZ$",
                                    "buyinvite.co.nz",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/")} );
        } else if (BuildConfig.FLAVOR.equals("ozsaleRC")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Australia",
                                        "AS",
                                        "A816D792-E940-44F0-B752-06DE1ED5C2B9",
                                        "$",
                                        "ozsale.com.au",
                                        "EN",
                                        "https://www.ozsale.com.au/",
                                        "https://www.ozsale.com.au/")} );
        } else if (BuildConfig.FLAVOR.equals("ozsaleTest")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Australia",
                                    "AS",
                                    "A816D792-E940-44F0-B752-06DE1ED5C2B9",
                                    "$",
                                    "ozsale.com.au",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/")} );
        } else if (BuildConfig.FLAVOR.equals("singsaleRC")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Singapore",
                                        "SI",
                                        "0508BB90-AFA8-435A-8EEA-FB27F500FBF7",
                                        "S$",
                                        "singsale.com.sg",
                                        "EN",
                                        "https://www.singsale.com.sg/",
                                        "https://www.singsale.com.sg/")} );
        } else if (BuildConfig.FLAVOR.equals("singsaleTest")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Singapore",
                                    "SI",
                                    "0508BB90-AFA8-435A-8EEA-FB27F500FBF7",
                                    "S$",
                                    "singsale.com.sg",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/")} );
        } else if (BuildConfig.FLAVOR.equals("dealsDirectRC")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Australia",
                                        "DA",
                                        "A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C",
                                        "$",
                                        "dealsdirect.com.au",
                                        "EN",
                                        "https://www.dealsdirect.com.au/",
                                        "https://www.dealsdirect.com.au/")} );
        } else if (BuildConfig.FLAVOR.equals("dealsDirectTest")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Australia",
                                    "DA",
                                    "A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C",
                                    "$",
                                    "dealsdirect.com.au",
                                    "EN",
                                    "https://api.mysaledev.com/",
                                    "https://api.mysaledev.com/")} );
        } else if (BuildConfig.FLAVOR.equals("topbuyRC")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Australia",
                                        "TA",
                                        "909E0AC5-908A-4A73-8E5C-5312DEEBD24C",
                                        "$",
                                        "topbuy.com.au",
                                        "EN",
                                        "https://www.topbuy.com.au/",
                                        "https://www.topbuy.com.au/")} );
        } else if (BuildConfig.FLAVOR.equals("topbuyTest")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Australia",
                                    "TA",
                                    "909E0AC5-908A-4A73-8E5C-5312DEEBD24C",
                                    "$",
                                    "topbuy.com.au",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/")} );
        } else if (BuildConfig.FLAVOR.equals("mysaleRC")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Philippines",
                                        "PH",
                                        "4EBF093C-0D81-43E5-A284-CB5F24F7361C",
                                        "₱",
                                        "mysale.ph",
                                        "EN",
                                        "https://www.mysale.ph/",
                                        "https://www.mysale.ph/"),
                            new Country("Thailand",
                                        "TH",
                                        "28F20D19-6E9E-4B43-98AB-765F96DC0E5C",
                                        "฿",
                                        "mysale.co.th",
                                        "EN",
                                        "https://www.mysale.co.th/",
                                        "https://www.mysale.co.th/"),
                            new Country("Malaysia",
                                        "MY",
                                        "34849BC9-EB96-4E2B-9698-B71F11F73297",
                                        "RM",
                                        "mysale.my",
                                        "EN",
                                        "https://www.mysale.my/",
                                        "https://www.mysale.my/"),
                            new Country("United Kingdom",
                                        "UK",
                                        "314D2B32-21F9-431D-975C-129BE4ED6BA0",
                                        "£",
                                        "mysale.co.uk",
                                        "EN",
                                        "https://www.mysale.co.uk/",
                                        "https://www.mysale.co.uk/"),
                            new Country("HongKong",
                                        "HK",
                                        "ED03076F-912B-4287-9580-6E2F52D33580",
                                        "HK$",
                                        "mysale.hk",
                                        "EN",
                                        "https://www.mysale.hk/",
                                        "https://www.mysale.hk/")} );
        } else if (BuildConfig.FLAVOR.equals("mysaleTest")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Philippines",
                                    "PH",
                                    "4EBF093C-0D81-43E5-A284-CB5F24F7361C",
                                    "₱",
                                    "mysale.ph",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/"),
                            new Country("Thailand",
                                    "TH",
                                    "28F20D19-6E9E-4B43-98AB-765F96DC0E5C",
                                    "฿",
                                    "mysale.co.th",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/"),
                            new Country("Malaysia",
                                    "MY",
                                    "34849BC9-EB96-4E2B-9698-B71F11F73297",
                                    "RM",
                                    "mysale.my",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/"),
                            new Country("United Kingdom",
                                    "UK",
                                    "314D2B32-21F9-431D-975C-129BE4ED6BA0",
                                    "£",
                                    "mysale.co.uk",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/"),
                            new Country("HongKong",
                                    "EN",
                                    "ED03076F-912B-4287-9580-6E2F52D33580",
                                    "HK$",
                                    "mysale.hk",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/")} );
        } else if (BuildConfig.FLAVOR.equals("nzsaleRC")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("New Zealand",
                                        "NZ",
                                        "B31F92DA-08DF-40A8-B156-B145A9B209D0",
                                        "NZ$",
                                        "nzsale.co.nz",
                                        "EN",
                                        "https://www.nzsale.co.nz/",
                                        "https://www.nzsale.co.nz/")} );
        } else if (BuildConfig.FLAVOR.equals("nzsaleTest")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("New Zealand",
                                    "NZ",
                                    "B31F92DA-08DF-40A8-B156-B145A9B209D0",
                                    "NZ$",
                                    "nzsale.co.nz",
                                    "EN",
                                    "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/")} );
        } else if (BuildConfig.FLAVOR.equals("thaisaleRC")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Thailand",
                                    "TH",
                                    "28F20D19-6E9E-4B43-98AB-765F96DC0E5C",
                                    "฿",
                                    "mysale.co.th",
                                    "EN",
                                    "https://www.mysale.co.th/",
                                    "https://www.mysale.co.th/")} );
        }
        else if (BuildConfig.FLAVOR.equals("thaisaleTest")){
            populatePackageWithCountries(
                    new Country[] {
                            new Country("Thailand",
                                        "TH",
                                        "28F20D19-6E9E-4B43-98AB-765F96DC0E5C",
                                        "฿",
                                        "mysale.co.th",
                                        "EN",
                                        "https://genie-ui-"+ BuildConfig.APP_NAME +"-pre.mysaledev.com/",
                                        "https://api.mysaledev.com/")} );
        }

        if (!getIsMultiCountry()) setCountry(Settings.getDefaultCountry());

        setupReCaptchaSiteKey();
    }

    static void populatePackageWithCountries(Country[] countries) {
        supportedCountries = countries;
    }

    static void setupReCaptchaSiteKey() {
        if (BuildConfig.IS_TEST) {
            reCaptchaSiteKey = "6LdvI6cUAAAAAIO16n0Sj8nQ4HNX1WEf6m27eRzb";
        } else {
            reCaptchaSiteKey = "6LehI6cUAAAAACrjaAGPQLQx1eomvLqrb0S_QxSi";
        }
    }
}
