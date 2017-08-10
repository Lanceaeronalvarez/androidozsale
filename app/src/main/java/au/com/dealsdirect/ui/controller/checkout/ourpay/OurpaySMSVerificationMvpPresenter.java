package au.com.dealsdirect.ui.controller.checkout.ourpay;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.ui.base.MvpPresenter;

public interface OurpaySMSVerificationMvpPresenter<V extends OurpaySMSVerificationMvpView> extends MvpPresenter<V> {

    void callNormalizePhone(String phone);

    void callVerificationCodeSend(String phone, String countryCode);

    void callVerificationCodeConfirm(String phone, String countryCode, String code);
}
