package au.com.dealsdirect.ui.main;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.content.Context;

import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.di.PerActivity;
import au.com.dealsdirect.ui.base.MvpPresenter;

@PerActivity
public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    void callAppSettingsWithAuthCheck();

    void callGetServerSettings();

    void callGetAppSettings();

    void callGetPublicAppSettings();

    void callGetAppSettingsSection(Context context);

    void fetchBTAuthorization();

    void callCreatePaymentTransaction(String deviceData, String paymentType, String paymentNonce, String paymentToken);

    void createPaymentMethod(String deviceData, String paymentNonce, String paymentType);

    void callLoginTicket();

    void callLogout(AuthHandler handler);

    void callGetTemplateTexts();

    String getStoredTemplateTexts(String detailKey);

    void initializeNotifications(Context context);

    String getKountMerchantId();

    boolean isAuthorized();

}
