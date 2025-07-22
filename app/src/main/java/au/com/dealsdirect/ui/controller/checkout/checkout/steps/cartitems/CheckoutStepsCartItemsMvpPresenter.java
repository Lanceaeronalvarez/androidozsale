package au.com.dealsdirect.ui.controller.checkout.checkout.steps.cartitems;

import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.data.templatetexts.TemplateTextsHelper;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

public interface CheckoutStepsCartItemsMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    CartDetailsMapper getCart();

    void fetchAdjustItemQuantity(String url, String itemID, String postcode);

    boolean isShippingByPostcodeEnabled();

    String getImpossibleToDeliverAtLocationText();

    String getUnavailableText();
}
