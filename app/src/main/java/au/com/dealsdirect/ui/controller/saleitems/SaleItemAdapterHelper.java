package au.com.dealsdirect.ui.controller.saleitems;

import android.graphics.drawable.Drawable;

import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;

public interface SaleItemAdapterHelper {
    void onClickFreeDelivery(String deliveryThreshold, String deliveryType);

    boolean isProductInWishlist(SaleItemProduct item);

    boolean isGoogleAdsEnabled();

    void onItemClicked(
            int position,
            Drawable imagePlaceholderDrawable,
            String imageUrl,
            SaleItemProduct product,
            int viewLeft,
            int viewTop,
            int viewWidth,
            int viewHeight);

    void addToWishlist(SaleItemProduct item, SaleItemsMvpPresenter.WishlistDelayedCallback wishlistDelayedCallback);

    void removeFromWishlist(SaleItemProduct item, SaleItemsMvpPresenter.WishlistDelayedCallback wishlistDelayedCallback);

    void onPriceInfoClicked(SaleItemProduct item);
}
