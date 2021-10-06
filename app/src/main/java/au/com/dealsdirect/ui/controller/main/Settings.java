package au.com.dealsdirect.ui.controller.main;

import au.com.dealsdirect.BuildConfig;

/**
 * Created by MTC on 9/4/18.
 */


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

    private static final String ooAURoot = "https://www.oo.com.au/";
    private static final String buyinviteAURoot = "https://www.buyinvite.com.au/";
    private static final String buyinviteNZRoot = "https://www.buyinvite.co.nz/";
    private static final String ozsaleAURoot = "https://www.ozsale.com.au/";
    private static final String singsaleSGRoot = "https://www.singsale.com.sg/";
    private static final String dealsdirectAURoot = "https://www.dealsdirect.com.au/";
    private static final String topbuyAURoot = "https://www.topbuy.com.au/";
    private static final String mysalePHRoot = "https://www.mysale.ph/";
    private static final String mysaleTHRoot = "https://www.mysale.co.th/";
    private static final String mysaleMYRoot = "https://www.mysale.my/";
    private static final String mysaleUKRoot = "https://www.mysale.co.uk/";
    private static final String mysaleHKRoot = "https://www.mysale.hk/";
    private static final String nzsaleNZRoot = "https://www.nzsale.co.nz/";

    static void load() {
       if (BuildConfig.FLAVOR.equals("ooRC")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Australia",
                                    "OA",
                                    "25D201EB-9A1C-4045-90B6-E6A9F122C7CB",
                                    "$",
                                    "oo.com.au",
                                    "EN",
                                    ooAURoot,
                                    ooAURoot,
                                    "AUD",
                                    ooAURoot)});

        } else if (BuildConfig.FLAVOR.equals("ooTest")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Australia",
                                    "OA",
                                    "25D201EB-9A1C-4045-90B6-E6A9F122C7CB",
                                    "$",
                                    "oo.com.au",
                                    "EN",
                                    "https://www.oa.mysaledev.com/",
                                    "https://www.oa.mysaledev.com/",
                                    "AUD",
                                    "https://www.oa.mysaledev.com/")});

        } else if (BuildConfig.FLAVOR.equals("buyinviteRC")) {
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
                                    buyinviteAURoot),

                            new Country("New Zealand",
                                    "BN",
                                    "461308FD-59C6-4BE1-A3D0-FCA74BCD338F",
                                    "NZ$",
                                    "buyinvite.co.nz",
                                    "EN",
                                    buyinviteNZRoot,
                                    buyinviteNZRoot,
                                    "NZD",
                                    buyinviteNZRoot)});

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
                                    "https://www.ba.mysaledev.com/"),

                            new Country("New Zealand",
                                    "BN",
                                    "461308FD-59C6-4BE1-A3D0-FCA74BCD338F",
                                    "NZ$",
                                    "buyinvite.co.nz",
                                    "EN",
                                    "https://www.bn.mysaledev.com/",
                                    "https://www.bn.mysaledev.com/",
                                    "NZD",
                                    "https://www.bn.mysaledev.com/")});
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
        } else if (BuildConfig.FLAVOR.equals("topbuyRC")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Australia",
                                    "TA",
                                    "909E0AC5-908A-4A73-8E5C-5312DEEBD24C",
                                    "$",
                                    "topbuy.com.au",
                                    "EN",
                                    topbuyAURoot,
                                    topbuyAURoot,
                                    "AUD",
                                    topbuyAURoot)});
        } else if (BuildConfig.FLAVOR.equals("topbuyTest")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Australia",
                                    "TA",
                                    "909E0AC5-908A-4A73-8E5C-5312DEEBD24C",
                                    "$",
                                    "topbuy.com.au",
                                    "EN",
                                    "https://www.ta.mysaledev.com/",
                                    "https://www.ta.mysaledev.com/",
                                    "AUD",
                                    "https://www.ta.mysaledev.com/")});
        } else if (BuildConfig.FLAVOR.equals("mysaleRC")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Philippines",
                                    "PH",
                                    "4EBF093C-0D81-43E5-A284-CB5F24F7361C",
                                    "₱",
                                    "mysale.ph",
                                    "EN",
                                    mysalePHRoot,
                                    mysalePHRoot,
                                    "PHP",
                                    mysalePHRoot),
                            new Country("Thailand",
                                    "TH",
                                    "28F20D19-6E9E-4B43-98AB-765F96DC0E5C",
                                    "฿",
                                    "mysale.co.th",
                                    "EN",
                                    mysaleTHRoot,
                                    mysaleTHRoot,
                                    "THB",
                                    mysaleTHRoot),
                            new Country("Malaysia",
                                    "MY",
                                    "34849BC9-EB96-4E2B-9698-B71F11F73297",
                                    "RM",
                                    "mysale.my",
                                    "EN",
                                    mysaleMYRoot,
                                    mysaleMYRoot,
                                    "MYR",
                                    mysaleMYRoot),
                            new Country("United Kingdom",
                                    "UK",
                                    "314D2B32-21F9-431D-975C-129BE4ED6BA0",
                                    "£",
                                    "mysale.co.uk",
                                    "EN",
                                    mysaleUKRoot,
                                    mysaleUKRoot,
                                    "GBP",
                                    mysaleUKRoot),
                            new Country("HongKong",
                                    "HK",
                                    "ED03076F-912B-4287-9580-6E2F52D33580",
                                    "HK$",
                                    "mysale.hk",
                                    "EN",
                                    mysaleHKRoot,
                                    mysaleHKRoot,
                                    "HKD",
                                    mysaleHKRoot)});
        } else if (BuildConfig.FLAVOR.equals("mysaleTest")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Philippines",
                                    "PH",
                                    "4EBF093C-0D81-43E5-A284-CB5F24F7361C",
                                    "₱",
                                    "mysale.ph",
                                    "EN",
                                    "https://www.ph.mysaledev.com/",
                                    "https://www.ph.mysaledev.com/",
                                    "PHP",
                                    "https://www.ph.mysaledev.com/"),
                            new Country("Thailand",
                                    "TH",
                                    "28F20D19-6E9E-4B43-98AB-765F96DC0E5C",
                                    "฿",
                                    "mysale.co.th",
                                    "EN",
                                    "https://www.th.mysaledev.com/",
                                    "https://www.th.mysaledev.com/",
                                    "THB",
                                    "https://www.th.mysaledev.com/"),
                            new Country("Malaysia",
                                    "MY",
                                    "34849BC9-EB96-4E2B-9698-B71F11F73297",
                                    "RM",
                                    "mysale.my",
                                    "MY",
                                    "https://www.my.mysaledev.com/",
                                    "https://www.my.mysaledev.com/",
                                    "MYR",
                                    "https://www.my.mysaledev.com/"),
                            new Country("United Kingdom",
                                    "UK",
                                    "314D2B32-21F9-431D-975C-129BE4ED6BA0",
                                    "£",
                                    "mysale.co.uk",
                                    "EN",
                                    "https://www.uk.mysaledev.com/",
                                    "https://www.uk.mysaledev.com/",
                                    "GBP",
                                    "https://www.uk.mysaledev.com/"),
                            new Country("HongKong",
                                    "HK",
                                    "ED03076F-912B-4287-9580-6E2F52D33580",
                                    "HK$",
                                    "mysale.hk",
                                    "EN",
                                    "https://www.hk.mysaledev.com/",
                                    "https://www.hk.mysaledev.com/",
                                    "HKD",
                                    "https://www.hk.mysaledev.com/")});
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
        } else if (BuildConfig.FLAVOR.equals("thaisaleRC")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Thailand",
                                    "TH",
                                    "28F20D19-6E9E-4B43-98AB-765F96DC0E5C",
                                    "฿",
                                    "mysale.co.th",
                                    "EN",
                                    "https://www.mysale.co.th/",
                                    "https://www.mysale.co.th/",
                                    "THB",
                                    "https://www.mysale.co.th/")});
        } else if (BuildConfig.FLAVOR.equals("thaisaleTest")) {
            populatePackageWithCountries(
                    new Country[]{
                            new Country("Thailand",
                                    "TH",
                                    "28F20D19-6E9E-4B43-98AB-765F96DC0E5C",
                                    "฿",
                                    "mysale.co.th",
                                    "EN",
                                    "https://genie-ui-" + BuildConfig.APP_NAME + "-pre.mysaledev.com/",
                                    "https://api.mysaledev.com/",
                                    "THB",
                                    "https://genie-ui-" + BuildConfig.APP_NAME + "-pre.mysaledev.com/")});
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


    // Below is for automation build purposes

    public static void main(String[] args) {

        load();
        setupReCaptchaSiteKey();

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
            case "PRINT_RECAPTCHA":
                System.out.println(reCaptchaSiteKey);
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
