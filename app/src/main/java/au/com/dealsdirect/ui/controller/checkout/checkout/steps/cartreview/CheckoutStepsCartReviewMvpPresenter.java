package au.com.dealsdirect.ui.controller.checkout.checkout.steps.cartreview;

import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

public interface CheckoutStepsCartReviewMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    CartDetailsMapper getCart();

    void adjustItemQuantity(String url, String itemID, String postcode);

    boolean isShippingByPostcodeEnabled();

    String getImpossibleToDeliverAtLocationText();

    String getUnavailableText();
}
