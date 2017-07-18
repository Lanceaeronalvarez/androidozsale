package au.com.dealsdirect.ui.main;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.di.PerActivity;
import au.com.dealsdirect.ui.base.MvpPresenter;

@PerActivity
public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    void callGetServerSettings();

    void callGetAppSettings();

    void callGetPublicAppSettings();

    void callGetAppSettingsSection();

    void fetchBTAuthorization();

    void callCreatePaymentTransaction(String deviceData, String paymentType, String paymentNonce, String paymentToken);

    void createPaymentMethod(String deviceData, String paymentNonce, String paymentType);

    void callLoginTicket();

    void callLogout();

    String getKountMerchantId();

    boolean isAuthorized();

}
