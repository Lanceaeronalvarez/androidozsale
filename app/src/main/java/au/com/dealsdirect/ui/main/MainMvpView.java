package au.com.dealsdirect.ui.main;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.mysale.genie.utility.config.model.getappsettingssection.Android;

import java.util.ArrayList;

import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.service.braintree.FetchBraintreeClientTokenHandler;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.utils.AppConstants;

public interface MainMvpView extends MvpView {


    void callGCMRegisterSubscriber();

    void storeTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue templateKeysValue);

    Router getCurrentRouter();

    Controller getCurrentController(Router router);

    // Authorization
    void showLoginController(Router router, AuthHandler handler);

    void loginSuccessHandler(Router router, AppConstants.POP_FLAG flag, AppConstants.AUTH_FLAG authFlag);

    void loginErrorHandler(String message);

    void logLoginTicket();

    void callLoginTicket(boolean isGdprCountry);

    void callLogout(AuthHandler handler);

    // Braintree methods
    void onBraintreeAuthorizationFetchSuccess(String paymentToken, String paymentMethod);

    void onBraintreeAuthorizationFetchFail();

    void performBraintreeReset();

    void performResetWithAuthFetch();

    void fetchBraintreeAuthorization(FetchBraintreeClientTokenHandler fetchBraintreeClientTokenHandler);

    void setPaymentMethodSelected(PaymentMethod paymentMethodSelected);

    boolean isBraintreeInitialized();

//    void setVisaCheckoutActionType(int visaCheckoutActionType);

//    int getVisaCheckoutActionType();

    // Payment methods
    void showGetPaymentMethodNonceSuccess(String nonce);

    void showCreatePaymentMethodSuccess(PaymentMethod lastPaymentMethod);

    void callCreatePaymentTransaction(String type, String nonce, String token);

    //void callCreatePaymentTransactionVco(VisaPaymentSummary visaPaymentSummary);

    void callCreatePaymentTransactionStripe(String paymentType, String paymentMethodId);

    void showCreatePaymentTransactionSuccess(String paymentType, CreatePaymentTransaction.ResponseValue responseValue);

    void showCreatePaymentTransactionFailure(String errorMessage);

    void refreshWishlist();

    void updateWishlistCounter(int count);

    PaymentMethod getPaymentMethodSelected();

    boolean getIsMyPayEnabled();

    void startPaypalPayment();

    void callApiSettings();

    // GDPR

    void showStrictConsentUI();

    void onClickAgreeStrictConsentUI();

    void showIntrospectionUtils(ArrayList<Android> androidArrayList);

    void onGetAppSettings();

    void show3DSecureStripe(String clientSecret);

    void showErrorMessage(String errorMessage);
}
