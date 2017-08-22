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

public interface MainMvpView extends MvpView, BrainTreeListeners {

    void showLoginController(Router router, AuthHandler handler);

    void onAuthorizationFetched(String paymentToken, String paymentMethod);

    void performReset();

    void performResetWithAuthFetch();

    void fetchAuthorization(FetchTokenHandler fetchTokenHandler);

    void setPaymentMethodSelected(PaymentMethod paymentMethodSelected);

    FetchTokenHandler getFetchTokenHandler();

    void callLoginTicket();

    void callGCMRegisterSubscriber();

    void callLogout(AuthHandler handler);

    void createPaymentMethodSuccess(PaymentMethod lastPaymentMethod);

    void callCreatePaymentTransaction(String paymentNonce);

    void callCreatePaymentTransaction(String paymentType, String paymentNonce);

    void callCreatePaymentTransactionError(String errorMessage);

    void storeTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue templateKeysValue);

    void createPaymentTransactionSuccess(String paymentType, CreatePaymentTransaction.ResponseValue responseValue);

    Router getCurrentRouter();

    Controller getCurrentController(Router router);


}
