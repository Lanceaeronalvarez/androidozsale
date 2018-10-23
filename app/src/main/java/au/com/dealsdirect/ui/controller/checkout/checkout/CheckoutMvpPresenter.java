package au.com.dealsdirect.ui.controller.checkout.checkout;

import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.ui.base.BaseActivity;
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

    void generateOurpay(Value value);

    void facebookInitiatedCheckout(String paymentType,
                                   int numItems,
                                   double price);

    void updateCart(GetCurrentOrder.ResponseValue responseValue);

    void updateCartValues(Value cartDetailsValue);

    void setDeliveryOption(SetDeliveryOption.OptionParameters setDeliveryOptionParameters);

    boolean isMasterPassEnabled();

    boolean isPaypalCreditEnabled();

    boolean isPaypalEnabled();

    boolean isVcoEnabled();
}
