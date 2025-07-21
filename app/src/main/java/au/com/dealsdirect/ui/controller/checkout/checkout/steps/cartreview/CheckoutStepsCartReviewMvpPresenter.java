package au.com.dealsdirect.ui.controller.checkout.checkout.steps.cartreview;

import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.data.templatetexts.TemplateTextsHelper;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;

public interface CheckoutStepsCartReviewMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    CartDetailsMapper getCart();

    void fetchAdjustItemQuantity(String url, String itemID, String postcode);

    boolean isShippingByPostcodeEnabled();

    TemplateTextsHelper.TemplateTextsRepository getTemplateTextsRepository();

}
