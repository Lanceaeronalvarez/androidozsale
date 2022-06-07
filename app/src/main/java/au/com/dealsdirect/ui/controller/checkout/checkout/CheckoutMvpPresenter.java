package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.content.Context;

import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.klarna.KlarnaCreateOrderRequest;
import au.com.dealsdirect.data.network.model.checkout.klarna.KlarnaCreateSessionRequest;
import au.com.dealsdirect.data.templatetexts.TemplateTextsHelper;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface CheckoutMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void callCartContent(String postcode);

    void fetchUserPaymentMethods();

    void fetchAdjustItemQuantity(String url, String itemID, String postcode, ProductQuantityLayout view);

    boolean isCartAlreadyLoadedOnce();

    void resetIsCartAlreadyLoaded();

    boolean checkIsLoggedIn();

    void generateOurpay(CheckoutDetailsMapper value);

    void logInitiateCheckout(Context context,
                             String paymentType,
                             int numItems,
                             double price,
                             String selectedPaymentType);

    void logCommonCheckoutEvent(Context context, int operation);

    void logFailedTransaction(Context context, String errorMessage);

    void updateCartValues(CheckoutDetailsMapper mappedValues);

    void setDeliveryOption(SetDeliveryOption.OptionParameters setDeliveryOptionParameters, String postcode);

    String getAfterpayLightboxImgUrl();

    String getAfterpayTermsLink();

    boolean isMasterPassEnabled();

    boolean isPaypalCreditEnabled();

    boolean isPaypalEnabled();

    boolean isVcoEnabled();

    boolean isShippingByPostcodeEnabled();

    String stripePaymentMethodId();

    void setStripePaymentMethodId(String paymentMethodId);

    boolean isStripeEnabled();

    boolean isKlarnaEnabled();

    String getStripePublicKey();

    TemplateTextsHelper.TemplateTextsRepository getTemplateTextsRepository();

    void saveAgeRestrictionData(String date, String postcode);
}
