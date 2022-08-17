package au.com.dealsdirect.ui.controller.saleitems;

import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface SaleItemsMvpPresenter<V extends SaleItemsMvpView> extends MvpPresenter<V> {

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

    void loadProductDetails(String saleId, String seoIdentifier);

    void loadSortingFacets();

    boolean isSortingEnabled();

    boolean isGoogleAdsEnabled();

    int getColumnCount();

    void setColumnCount(int columnCount);

    void setTimeStamp(String date);

    String getTimeStamp();

    void loadBrandBubbles();

    long getSupplierOriginaPriceInfoTimeAgreed();

    void setSupplierOriginaPriceInfoTimeAgreed(long timestamp);

    int getPriceBlockMode();
}
