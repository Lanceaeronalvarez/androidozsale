package au.com.dealsdirect.ui.main;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.content.Context;

import com.visa.checkout.VisaPaymentSummary;

import au.com.dealsdirect.data.auth.AuthHandler;
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

    void callGetAppSettingsSectionsPayments(Context context);

    void callGetPublicAppSettingsSectionsLPay(Context context);

    void callGetAppSettingsSectionsZipPay(Context context);

    void callGetAppSettingsConsent(Context context);

    void callGetPublicAppSettingsConsent(Context context);

    void checkConsentCookie();

    void callGetConsentData();

    void callSaveConsentData();

    void callGetWishlistIdsOnly();

    void showStrictConsentUI();

    void callGetPublicPaymentToken();

    void fetchBraintreeClientToken();

    void createPaymentTransaction(String deviceData, String paymentType, String paymentNonce, String paymentToken, String provider);

    void createPaymentTransactionGPay(String token);

    void createPaymentTransactionVco(VisaPaymentSummary visaPaymentSummary);

    void createPaymentTransactionStripe(String paymentType, String paymentMethodId, String provider);

    void createPaymentTransactionStripePaymentIntent(String paymentType, String paymentMethodId);

    void createPaymentMethod(String deviceData, String paymentNonce, String paymentType);

    void createPaymentMethodStripe(String type, String token);

    void callLoginTicket(Context context, boolean isGdprCountry);

    void callLogout(AuthHandler handler);

    void callGetTemplateTexts();

    void initializeNotifications(Context context);

    String getKountMerchantId();

    boolean isAuthorized();

    void callGetPaymentMethodNonce(String token);

    void callGCMNotificationEvent(Context context);

    void callApiSettings(Context context);

    void callGetAccountData();

    void initFacebookAnalytics();

    void initializeAnalytics(Context activityContext, Context applicationContext);

    boolean isDebug();

    String defaultCountryId();

    void setCountry(Settings.Country country);

    String legacyCountryId();

    void setUserRateCurrentVersion(boolean userRateCurrentVersion);

    boolean isUserRateCurrentVersion();

    boolean shouldShowStrictConsent();

    void callFileSettings();

    boolean doesCheckoutHaveWishlistItem();

    String stripePublicKey();

    void setPaymentMethodId(String paymentMethodId);

    boolean isStripeEnabled();

    void storeCachedResponses();

    void fetchCachedResponses();

    void pruneCachedResponses();

    void callGetUserCurrent();

    boolean getSupplierOriginalPriceInfoEnabled();
}
