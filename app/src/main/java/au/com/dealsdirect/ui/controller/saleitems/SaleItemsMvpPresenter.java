package au.com.dealsdirect.ui.controller.saleitems;

import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface SaleItemsMvpPresenter<V extends SaleItemsMvpView> extends MvpPresenter<V> {

    void loadWishlistPaginated(int limit, int offset);

    boolean isProductInWishlist(String productId);

    int wishlistCount();

    void addToWishlist(String productId, String productName, String seoIdentifier, Double price, WishlistDelayedCallback delayedCallback);

    void removeFromWishlist(String productId, String productName, Double price, WishlistDelayedCallback delayedCallback);

    public interface WishlistDelayedCallback {
        void performDelayedAction(String productId, String productName, Double price, boolean isLiked);
    }

    void loadSaleBannerDetails(String saleId);

    void loadSaleItems(GetSaleItemsRequest getSaleItemsRequest);

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

    int getHoursLeftToDisplayTimer();

    void loadLeaderboardBanner();

    void getPricingInfoText(String seoIdentifier, String saleId);

    String getShippingTemplateText();

    String getShippingTitleText();
}
