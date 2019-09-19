package au.com.dealsdirect.ui.main;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.content.Context;

import com.visa.checkout.VisaPaymentSummary;

import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.country.Country;
import au.com.dealsdirect.data.network.model.orders.CreateRefundRequest;
import au.com.dealsdirect.di.PerActivity;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.controller.main.Settings;

@PerActivity
public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    void callGetServerSettings();

    void callGetAppSettings();

    void callGetPublicAppSettings();

    void callGetAppSettingsSection(Context context);

    void callGetPublicAppSettingsSections(Context context);

    void callGetPublicAppSettingsSectionsAfterpay(Context context);

    void callGetAppSettingsConsent(Context context);

    void callGetPublicAppSettingsConsent(Context context);

    void checkConsentCookie();

    void callGetConsentData();

    void callSaveConsentData();

    void showStrictConsentUI();

    void callGetPublicPaymentToken();

    void fetchBTAuthorization();

    void createPaymentTransaction(String deviceData, String paymentType, String paymentNonce, String paymentToken);

    void createPaymentTransactionVco(VisaPaymentSummary visaPaymentSummary);

    void createPaymentMethod(String deviceData, String paymentNonce, String paymentType);

    void callLoginTicket(Context context, boolean isGdprCountry);

    void callLogout(AuthHandler handler);

    void callGetTemplateTexts();

    String getStoredTemplateTexts(String detailKey);

    boolean getIsMyPayEnabled();

    void initializeNotifications(Context context);

    String getKountMerchantId();

    boolean isAuthorized();

    void callGetPaymentMethodNonce(String token);

    void callGCMNotificationEvent(Context context);

    void callApiSettings(Context context);

    void callGetAccountData();

    void initFacebookAnalytics();

    void initializeAnalytics(Context activityContext, Context applicationContext);

    void getDeepLinkData(String url);

    void deepLinkMessageThread();

    void deepLinkSaleItems();

    boolean isDebug();

    String defaultCountryId();

    void setCountry(Settings.Country country);

    String legacyCountryId();

    void setUserRateCurrentVersion(boolean userRateCurrentVersion);

    boolean isUserRateCurrentVersion();

    boolean shouldShowStrictConsent();

    void callFileSettings();
}
