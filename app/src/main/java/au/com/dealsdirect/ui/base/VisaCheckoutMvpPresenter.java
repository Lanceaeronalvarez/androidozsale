package au.com.dealsdirect.ui.base;

import com.visa.checkout.VisaPaymentSummary;

import au.com.dealsdirect.data.network.model.login.LoginVisa;

public interface VisaCheckoutMvpPresenter<V extends VisaCheckoutMvpView> extends MvpPresenter<V> {

    void setupVisaCheckout(boolean isFromCheckout);

    void loginWithVisaCheckout();

    void payWithVisaCheckout(Double cartTotal);

    void authenticateLoginWithVisaCheckoutNative(VisaPaymentSummary visaPaymentSummary);

    void authenticateLoginWithVisaCheckoutBraintree(String firstname, String lastName, String email, String callId, String paymentNonce);

    void executeLoginVisa(LoginVisa.RequestValue.Data requestData, String password);

    void executeLoginVisa(LoginVisa.RequestValue.Data requestData, String password, boolean tncAccepted, boolean emailsAccepted);

    boolean isVisaCheckoutEnabled();

    void initializeBraintree();
}
