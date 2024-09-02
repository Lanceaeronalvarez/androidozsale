package au.com.dealsdirect.ui.controller.shops;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface ShopsMvpPresenter<V extends ShopsMvpView> extends MvpPresenter<V> {

    void loadShopsBanner(GetBannerRequest request);

    void loadShopsBanner(GetBannerRequest request, boolean getOnlyFromNetwork);

    void loadSlidingBanners(GetBannerRequest request);

    void loadSponsoredBanners(GetBannerRequest request);

    void loadTrendingBrands(GetBannerRequest request);

    void loadBestSellers(String category);

    void loadCategoryTree();

    boolean isAccessAnonymousEnabled();

    void cancelRequest();

    boolean isAuthorized();

    boolean isGoogleAdsEnabled();

    int getBannerColumnCount();

    boolean getPrefersOldShopBannerDimensions();

    void setPrefersOldShopBannerDimensions(boolean doesPrefer);

    void loadLeaderboardBanner(String categoryId);

    int wishlistCount();

    boolean isProductInWishlist(String productId);

    void addProductToWishlist(String productId, String seoIdentifier, String masterProductId, WishlistDelayedCallback delayedCallback);

    void removeProductFromWishlist(String productId, WishlistDelayedCallback delayedCallback);

    void getPricingInfoText(String seoIdentifier);

    interface WishlistDelayedCallback {
        void performDelayedAction();
    }
}
