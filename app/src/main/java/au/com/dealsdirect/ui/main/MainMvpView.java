package au.com.dealsdirect.ui.main;
/*
 * Created by CodeineBot on 5/15/17.
 */


import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;

import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.utils.AppConstants;

public interface MainMvpView extends MvpView, BrainTreeListeners {

    void showLoginController(Router router, AuthHandler handler);

    DefaultCallback getFetchTokenHandler();

    void callLoginTicket();

    void callGCMRegisterSubscriber();

    void callLogout(AuthHandler handler);

    void storeTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue templateKeysValue);

    Router getCurrentRouter();

    Controller getCurrentController(Router router);

    void loginSuccessHandler(Router router, AppConstants.POP_FLAG flag, AppConstants.AUTH_FLAG authFlag);

    void loginErrorHandler(String message);


    // Braintree methods
    void onAuthorizationFetched(String paymentToken, String paymentMethod);
    void performBraintreeReset();
    void performResetWithAuthFetch();
    void fetchAuthorization(DefaultCallback fetchTokenHandler);
    void setPaymentMethodSelected(PaymentMethod paymentMethodSelected);

    // Payment methods
    void showGetPaymentMethodNonceSuccess(String nonce);
    void callCreatePaymentMethod(String type, String nonce);
    void showCreatePaymentMethodSuccess(PaymentMethod lastPaymentMethod);
    void callCreatePaymentTransaction(String type, String nonce, String token);
    void showCreatePaymentTransactionSuccess(String paymentType, CreatePaymentTransaction.ResponseValue responseValue);
    void showCreatePaymentTransactionFailure(String errorMessage);

}
