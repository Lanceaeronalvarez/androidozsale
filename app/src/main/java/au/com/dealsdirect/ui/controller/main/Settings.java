package au.com.dealsdirect.ui.controller.main;

import au.com.dealsdirect.BuildConfig;

public class Settings {

    public static class Country {
        public String countryName;
        public String countryId;
        public String accountId;
        public String currencySign;
        public String siteName;
        public String languageId;
        public String genieRoot;
        public String legacyRoot;
        public String currencyCode;
        public String myAccount;

        Country(String countryName, String countryId, String accountId, String currencySign, String siteName,
                String languageId, String genieRoot, String legacyRoot, String currencyCode, String myAccount) {
            this.countryName = countryName;
            this.countryId = countryId;
            this.accountId = accountId;
            this.currencySign = currencySign;
            this.siteName = siteName;
            this.languageId = languageId;
            this.genieRoot = genieRoot;
            this.legacyRoot = legacyRoot;
            this.currencyCode = currencyCode;
            this.myAccount = myAccount;
        }
    }

    private static Country selectedCountry;

    private static Country[] supportedCountries;

    public static Country getSelectedCountry() {
        return selectedCountry;
    }

    public static void setCountry(Country newCountry) {
        selectedCountry = newCountry;
    }

    public static boolean getIsMultiCountry() {
        return supportedCountries.length > 1;
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

    private static final String buyinviteAURoot = "https://www.buyinvite.com.au/";
    private static final String ozsaleAURoot = "https://www.ozsale.com.au/";
    private static final String singsaleSGRoot = "https://www.singsale.com.sg/";
    private static final String dealsdirectAURoot = "https://www.dealsdirect.com.au/";
    private static final String mysaleMYRoot = "https://www.mysale.my/";
    private static final String nzsaleNZRoot = "https://www.nzsale.co.nz/";

    static void load() {
        if (BuildConfig.FLAVOR.equals("buyinviteRC")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Australia",
                                    "BA",
                                    "FD6E7F98-F8B4-49D9-8FEF-D1AA02BCB43A",
                                    "$",
                                    "buyinvite.com.au",
                                    "EN",
                                    buyinviteAURoot,
                                    buyinviteAURoot,
                                    "AUD",
                                    buyinviteAURoot)});

        } else if (BuildConfig.FLAVOR.equals("buyinviteTest")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Australia",
                                    "BA",
                                    "FD6E7F98-F8B4-49D9-8FEF-D1AA02BCB43A",
                                    "$",
                                    "buyinvite.com.au",
                                    "EN",
                                    "https://www.ba.mysaledev.com/",
                                    "https://www.ba.mysaledev.com/",
                                    "AUD",
                                    "https://www.ba.mysaledev.com/")});
        } else if (BuildConfig.FLAVOR.equals("ozsaleRC")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Australia",
                                    "AS",
                                    "A816D792-E940-44F0-B752-06DE1ED5C2B9",
                                    "$",
                                    "ozsale.com.au",
                                    "EN",
                                    ozsaleAURoot,
                                    ozsaleAURoot,
                                    "AUD",
                                    ozsaleAURoot)});
        } else if (BuildConfig.FLAVOR.equals("ozsaleTest")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Australia",
                                    "AS",
                                    "A816D792-E940-44F0-B752-06DE1ED5C2B9",
                                    "$",
                                    "ozsale.com.au",
                                    "EN",
                                    "https://www.oz.mysaledev.com/",
                                    "https://www.oz.mysaledev.com/",
                                    "AUD",
                                    "https://www.oz.mysaledev.com/")});
        } else if (BuildConfig.FLAVOR.equals("singsaleRC")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Singapore",
                                    "SI",
                                    "0508BB90-AFA8-435A-8EEA-FB27F500FBF7",
                                    "S$",
                                    "singsale.com.sg",
                                    "EN",
                                    singsaleSGRoot,
                                    singsaleSGRoot,
                                    "SGD",
                                    singsaleSGRoot)});
        } else if (BuildConfig.FLAVOR.equals("singsaleTest")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Singapore",
                                    "SI",
                                    "0508BB90-AFA8-435A-8EEA-FB27F500FBF7",
                                    "S$",
                                    "singsale.com.sg",
                                    "EN",
                                    "https://www.si.mysaledev.com/",
                                    "https://www.si.mysaledev.com/",
                                    "SGD",
                                    "https://www.si.mysaledev.com/")});
        } else if (BuildConfig.FLAVOR.equals("dealsDirectRC")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Australia",
                                    "DA",
                                    "A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C",
                                    "$",
                                    "dealsdirect.com.au",
                                    "EN",
                                    dealsdirectAURoot,
                                    dealsdirectAURoot,
                                    "AUD",
                                    dealsdirectAURoot)});
        } else if (BuildConfig.FLAVOR.equals("dealsDirectTest")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Australia",
                                    "DA",
                                    "A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C",
                                    "$",
                                    "dealsdirect.com.au",
                                    "EN",
                                    "https://www.da.mysaledev.com/",
                                    "https://www.da.mysaledev.com/",
                                    "AUD",
                                    "https://www.da.mysaledev.com/")});
        } else if (BuildConfig.FLAVOR.equals("mysaleRC")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Malaysia",
                                    "MY",
                                    "34849BC9-EB96-4E2B-9698-B71F11F73297",
                                    "RM",
                                    "mysale.my",
                                    "EN",
                                    mysaleMYRoot,
                                    mysaleMYRoot,
                                    "MYR",
                                    mysaleMYRoot)});
        } else if (BuildConfig.FLAVOR.equals("mysaleTest")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Malaysia",
                                    "MY",
                                    "34849BC9-EB96-4E2B-9698-B71F11F73297",
                                    "RM",
                                    "mysale.my",
                                    "MY",
                                    "https://www.my.mysaledev.com/",
                                    "https://www.my.mysaledev.com/",
                                    "MYR",
                                    "https://www.my.mysaledev.com/")});
        } else if (BuildConfig.FLAVOR.equals("nzsaleRC")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("New Zealand",
                                    "NZ",
                                    "B31F92DA-08DF-40A8-B156-B145A9B209D0",
                                    "NZ$",
                                    "nzsale.co.nz",
                                    "EN",
                                    nzsaleNZRoot,
                                    nzsaleNZRoot,
                                    "NZD",
                                    nzsaleNZRoot)});
        } else if (BuildConfig.FLAVOR.equals("nzsaleTest")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("New Zealand",
                                    "NZ",
                                    "B31F92DA-08DF-40A8-B156-B145A9B209D0",
                                    "NZ$",
                                    "nzsale.co.nz",
                                    "EN",
                                    "https://www.nz.mysaledev.com/",
                                    "https://www.nz.mysaledev.com/",
                                    "NZD",
                                    "https://www.nz.mysaledev.com/")});
        }

        if (!getIsMultiCountry()) setCountry(Settings.getDefaultCountry());
    }

    static void populatePackageWithCountries(Country[] countries) {
        supportedCountries = countries;
    }

    // Below is for automation build purposes

    public static void main(String[] args) {

        load();

        String country = args[0];
        String appName = args[1];
        String task = args[2];

        switch (task) {
            case "PRINT_COUNTRY_ID":
                System.out.println(printSelectedCountryID(country, appName));
                break;
            case "PRINT_ACCOUNT_ID":
                System.out.println(printAccountId(country, appName));
                break;
            case "PRINT_GENIE_API":
                System.out.println(printGenieRoot(country, appName));
                break;
            case "PRINT_CURRENCY_CODE":
                System.out.println(printCurrencyCode(country, appName));
                break;
        }
    }

    private static String printSelectedCountryID(String country, String appName) {
        if (getIsMultiCountry()) {
            for (Country supportedCountry : supportedCountries) {
                if (supportedCountry.countryName.equalsIgnoreCase(country) && BuildConfig.FLAVOR.equalsIgnoreCase(appName)) {
                    return supportedCountry.countryId;
                }
            }
        } else {
            //if app is single country
            return supportedCountries[0].countryId;
        }
        return "";
    }

    private static String printAccountId(String country, String appName) {
        if (getIsMultiCountry()) {
            for (Country supportedCountry : supportedCountries) {
                if (supportedCountry.countryName.equalsIgnoreCase(country) && BuildConfig.FLAVOR.equalsIgnoreCase(appName)) {
                    return supportedCountry.accountId;
                }
            }
        } else {
            return supportedCountries[0].accountId;
        }
        return "";
    }

    private static String printGenieRoot(String country, String appName) {
        if (getIsMultiCountry()) {
            for (Country supportedCountry : supportedCountries) {
                if (supportedCountry.countryName.equalsIgnoreCase(country) && BuildConfig.FLAVOR.equalsIgnoreCase(appName)) {
                    return supportedCountry.genieRoot;
                }
            }
        } else {
            return supportedCountries[0].genieRoot;
        }
        return "";
    }

    private static String printCurrencyCode(String country, String appName) {
        if (getIsMultiCountry()) {
            for (Country supportedCountry : supportedCountries) {
                if (supportedCountry.countryName.equalsIgnoreCase(country) && BuildConfig.FLAVOR.equalsIgnoreCase(appName)) {
                    return supportedCountry.currencyCode;
                }
            }
        } else {
            return supportedCountries[0].currencyCode;
        }
        return "";
    }
}
