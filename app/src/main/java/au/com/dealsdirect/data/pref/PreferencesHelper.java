package au.com.dealsdirect.data.pref;


import com.mysale.genie.utility.config.api.GetAppSettingsConsent;

import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;

import java.util.HashSet;

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

    String getLegacyCountryId();

    String getLegacyLanguageId();

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

    void setIsMyPayEnabled(boolean isMyPayEnabled);

    boolean getIsMyPayEnabled();

    void setIsVisaCheckoutEnabled(boolean isVisaCheckoutEnabled);

    boolean getIsVisaCheckoutEnabled();

    void setVisaCheckoutApiKey(String visaCheckoutApiKey);

    String getVisaCheckoutApiKey();

    void setVisaCheckoutApiUrl(String visaCheckoutApiUrl);

    String getVisaCheckoutApiUrl();

    void setVisaCheckoutProviderType(int visaCheckoutProviderType);

    int getVisaCheckoutProviderType();

    void setPersonalisationTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value);

    String getPersonalisationTemplateTexts();

    void setConsentTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value);

    String getConsentTemplateTexts(String key);

    void setAppSettingsConsent(GetAppSettingsConsent.ResponseValue value);

    String getAppSettingsConsentText(String key);

    int getAppSettingsConsentMode();

    boolean getAppSettingsConsentIsChecked(String key);

    void setMyPayTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value);

    void setDeliveryOptionsTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value);

    void setEventUserId(String userId);

    String getEventUserId();

    HashSet<String> getCookies();

    String getMyPayTemplateTexts(String detailKey);

    void setIsInitialLaunch(boolean isInitialLaunch);

    boolean getIsInitialLaunch();

    void setIsMultiLanguage(boolean isMultiLanguage);

    boolean getIsMultiLanguage();

    void setIsMultiCountry(boolean isMultiCountry);

    boolean getIsMultiCountry();

    void setPublicPaymentToken(String publicPaymentToken);

    String getPublicPaymentToken();

    void setPublicPaymentType(String publicPaymentType);

    String getPublicPaymentType();

    void setIsNotificationsEnabled(boolean isNotificationsEnabled);

    boolean getIsNotificationsEnabled();

    void setIsPaypalCreditEnabled(boolean isPaypalCreditEnabled);

    boolean isPaypalCreditEnabled();

    void setIsSortingEnabled(boolean isSortingEnabled);

    boolean getIsSortingEnabled();

    void setLastRedirection(String lastRedirection);

    String getLastRedirection();

    void setIsNewUser(boolean isNewUser);

    boolean getIsNewUser();

    void setHasActiveCheckoutSession(boolean hasActiveCheckoutSession);

    boolean hasActiveCheckoutSession();

    void resetAddToCartJourneyFlags();

    void setHasViewedSale(boolean hasViewedSale);

    boolean hasViewedSale();

    void setHasViewedProductCategory(boolean hasViewedProductCategory);

    boolean hasViewedProductCategory();

    void setHasViewedProduct(boolean hasViewedProduct);

    boolean hasViewedProduct();

    void setHasAddedToCart(boolean hasAddedToCart);

    boolean hasAddedToCart();

    void setHasViewedCart(boolean hasViewedCart);

    boolean hasViewedCart();

    void setUserHasRateApp(boolean userHasRateApp);

    boolean userHasRateApp();

    void setShouldShowStrictConsent(boolean shouldShowStrictConsent);

    boolean shouldShowStrictConsent();

    void setIsOurpayDashboardEnabled(boolean enabled);
    boolean getIsOurpayDashboardEnabled();

    void setReCaptchaSiteKey(String key);
    String getReCaptchaSiteKey();

    void setIsGoogleAdsEnabled(boolean isGoogleAdsEnabled);

    boolean isGoogleAdsEnabled();

    void setLastColumnSelected(int columnCount);

    int getLastColumnSelected();

    void setLastTimeStamp(String timeStamp);

    String getLastTimeStamp();
}
