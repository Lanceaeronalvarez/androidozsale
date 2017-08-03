package au.com.dealsdirect.data.pref;


public interface PreferencesHelper {


    //Elv - add exposed methods here for AppPreferenceHelper
    int getCurrentUserLoggedInMode();

    //Config Prefs
    void setUserAgent();

    String getUserAgent();

    void setCountryId(String countryId);

    String getCountryId();

    void setLanguageId(String languageId);

    String getLanguageId();

    void setLanguages(String languagesJsonString);

    String getLanguages();

    void setSiteName(String siteName);

    String getSiteName();

    void setCurrency(String currency);

    String getCurrency();

    void setCurrencySign(String currencySign);

    String getCurrencySign();

    void setFollowUsFbLink(String followUsFbLink);

    String getFollowUsFbLink();

    void setFollowUsTwitterLink(String followUsTwitterLink);

    String getFollowUsTwitterLink();

    void setImageServerUrl(String imageServerUrl);

    String getImageServerUrl();

    void setIsPaypalEnabled(boolean val);

    boolean isPaypalEnabled();

    void setIsMasterpassEnabled(boolean val);

    boolean isMasterpassEnabled();

    void setIsAmexEnabled(boolean val);

    boolean isAmexEnabled();

    void setIsKountEnabled(boolean val);

    boolean isKountEnabled();

    void setKountMerchantId(String kountMerchantId);

    String getKountMerchantId();

    void setSearchMaxPrice(int searchMaxPrice);

    int getSearchMaxPrice();

    void setAccessAnonymousEnabled(boolean accessAnonymousEnabled);

    boolean getAccessAnonymousEnabled();

    void setFbSecret(String fbSecret);

    String getFbSecret();

    boolean isDebugMode();

    void setPaymentCount(int count);

    int getPaymentCount();

    void setGCMRegistrationId(String registrationId);

    String getGCMRegistrationId();

    void setGCMAppVersion(int appVersion);

    int getGCMAppVersion();


}
