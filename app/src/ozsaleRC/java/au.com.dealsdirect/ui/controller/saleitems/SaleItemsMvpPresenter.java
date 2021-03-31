package au.com.dealsdirect.ui.controller.saleitems;

import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;

import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface SaleItemsMvpPresenter<V extends SaleItemsMvpView> extends MvpPresenter<V> {

    void loadWishlistAll();

    void loadWishlistPaginated(int limit, int offset);

    boolean isProductInWishlist(String productId);

    int wishlistCount();

    void addToWishlist(String productId, String seoIdentifier, WishlistDelayedCallback delayedCallback);

    void removeFromWishlist(String productId, WishlistDelayedCallback delayedCallback);

    public interface WishlistDelayedCallback {
        void performDelayedAction(String productId, boolean isLiked);
    }

    void loadSaleBannerDetails(String saleId);

    void loadSaleItems(GetSaleItemsRequest getSaleItemsRequest);

    void loadProductDetails(RecyclerView.ViewHolder viewHolder,
                            int position,
                            String seoIdentifierId,
                            Drawable imagePlaceholderDrawable,
                            String imageUrl,
                            String skuId,
                            String saleId,
                            boolean isFreeDelivery,
                            String discountText,
                            String discountedPriceText,
                            boolean isSoldOut);

    void loadSortingFacets();

    boolean isSortingEnabled();

    boolean isGoogleAdsEnabled();

    int getColumnCount();

    void setColumnCount(int columnCount);

    void setTimeStamp(String date);

    String getTimeStamp();

    void loadBrandBubbles();
}
