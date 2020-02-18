package au.com.dealsdirect.ui.base;

import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.visa.checkout.Profile;
import com.visa.checkout.PurchaseInfo;

import au.com.dealsdirect.data.network.model.login.LoginVisa;

/**
 * Created by smartwave on 07/02/2018.
 */

public interface VisaCheckoutMvpView extends MvpView {

    int VISA_CHECKOUT_LOGIN = 0;
    int VISA_CHECKOUT_PAY = 1;

    void onSetupVisaCheckoutNative(Profile profile);

    void onSetupVisaCheckoutBraintree(String paymentToken, String paymentType, boolean isFromCheckout);

    void onStartVisaCheckoutIntent(PurchaseInfo purchaseInfo);

    void doAuthenticateLoginWithVisaCheckoutBraintree(VisaCheckoutNonce visaCheckoutNonce);

    void initializeVisaCheckoutButton(PurchaseInfo.PurchaseInfoBuilder purchaseInfoBuilder, boolean fromCheckout);

    void showPasswordVerification(LoginVisa.RequestValue.Data requestData, boolean isAccountExists, String accountEmail);

    void showLoginVisaSuccess(String loginTicket);

    void onVisaCheckoutButtonClicked();

    void setVisaCheckoutActionType(int visaCheckoutActionType);

    void initializeBrainTree(String token, String paymentType);
}
