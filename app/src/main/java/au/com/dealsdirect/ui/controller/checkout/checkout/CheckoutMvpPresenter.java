package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.content.Context;

import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface CheckoutMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void callCartContent();

    void fetchCartDetails();

    void fetchUserPaymentMethods();

    void fetchAdjustItemQuantity(String url, String itemID, ProductQuantityLayout view);

    boolean isCartAlreadyLoadedOnce();

    void resetIsCartAlreadyLoaded();

    boolean checkIsLoggedIn();

    void generateOurpay(CheckoutDetailsMapper value);

    void logInitiateCheckout(Context context,
                             String paymentType,
                             int numItems,
                             double price,
                             String selectedPaymentType);

    void updateCart(GetCurrentOrder.ResponseValue responseValue);

    void updateCartValues(CheckoutDetailsMapper mappedValues);

    void setDeliveryOption(SetDeliveryOption.OptionParameters setDeliveryOptionParameters);

    String getAfterpayLightboxImgUrl();

    String getAfterpayTermsLink();

    boolean isMasterPassEnabled();

    boolean isPaypalCreditEnabled();

    boolean isPaypalEnabled();

    boolean isVcoEnabled();

    String stripePaymentMethodId();

    void setStripePaymentMethodId(String paymentMethodId);

    boolean isStripeEnabled();

    String getStripePublicKey();
}
