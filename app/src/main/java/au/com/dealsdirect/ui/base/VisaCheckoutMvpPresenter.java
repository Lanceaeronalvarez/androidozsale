package au.com.dealsdirect.ui.base;

import android.view.View;

import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.visa.checkout.VisaPaymentSummary;

import au.com.dealsdirect.data.network.model.login.LoginVisa;

/**
 * Created by smartwave on 07/02/2018.
 */

public interface VisaCheckoutMvpPresenter<V extends VisaCheckoutMvpView> extends MvpPresenter<V>{

    void setupVisaCheckout();

    void loginWithVisaCheckout();

    void payWithVisaCheckout(Double cartTotal);

    void authenticateLoginWithVisaCheckoutNative(VisaPaymentSummary visaPaymentSummary);

    void authenticateLoginWithVisaCheckoutBraintree(VisaCheckoutNonce visaCheckoutNonce);

    void executeLoginVisa(LoginVisa.RequestValue.Data requestData, String password);

    void executeLoginVisa(LoginVisa.RequestValue.Data requestData, String password, boolean tncAccepted, boolean emailsAccepted);

    boolean isVisaCheckoutEnabled();

    void initializeBraintree();
}
