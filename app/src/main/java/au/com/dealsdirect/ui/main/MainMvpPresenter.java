package au.com.dealsdirect.ui.main;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.content.Context;

import com.visa.checkout.VisaPaymentSummary;

import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.di.PerActivity;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.MvpPresenter;

@PerActivity
public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    void callGetServerSettings();

    void callGetAppSettings();

    void callGetPublicAppSettings();

    void callGetAppSettingsSection(Context context);

    void callGetPublicPaymentToken();

    void fetchBTAuthorization();

    void createPaymentTransaction(String deviceData, String paymentType, String paymentNonce, String paymentToken);

    void createPaymentTransactionVco(VisaPaymentSummary visaPaymentSummary);

    void createPaymentMethod(String deviceData, String paymentNonce, String paymentType);

    void callLoginTicket();

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

    void initFacebookAnalytics();

    void initializeAnalytics(Context activityContext, Context applicationContext);

    boolean isDebug();
}
