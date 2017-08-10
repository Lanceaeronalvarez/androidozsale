package au.com.dealsdirect.ui.controller.checkout.ourpay;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.ourpayverificationcodeconfirm.VerificationCodeConfirmResponseBody;
import au.com.dealsdirect.data.network.model.ourpayverificationnormalizephone.VerificationNormalizePhoneResponseBody;
import au.com.dealsdirect.ui.base.MvpView;

public interface OurpaySMSVerificationMvpView extends MvpView {

    void loadExtension();

    void callNormalizePhoneResponse(VerificationNormalizePhoneResponseBody response);

    void callVerificationCodeSendResponse(VerificationNormalizePhoneResponseBody response);

    void callVerificationCodeConfirmResponse(VerificationCodeConfirmResponseBody response);

}
