package au.com.dealsdirect.ui.controller.checkout.checkout.empty;

import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpPresenter;

public interface CheckoutEmptyMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    CartDetailsMapper getCart();

    void loadCart(String postcode, String pickupPoint, boolean willForceLoad);

    boolean checkIsLoggedIn();
    void loadBestSellers(String category);
    void loadRecentlyViewedItems();

    boolean isProductInWishlist(String productId);

    int wishlistCount();

    void addProductToWishlist(String productId, String seoIdentifier, String masterProductId, CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback);

    void removeProductFromWishlist(String productId, CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback);

    void getPricingInfoText(String seoIdentifier);
}
