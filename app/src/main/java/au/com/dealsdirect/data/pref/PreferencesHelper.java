package au.com.dealsdirect.data.pref;


import com.mysale.genie.utility.config.model.getserversettings.Language;

import java.util.List;

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

    void setLanguages(List<Language> languages);

    List<Language> getLanguages();

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


}
