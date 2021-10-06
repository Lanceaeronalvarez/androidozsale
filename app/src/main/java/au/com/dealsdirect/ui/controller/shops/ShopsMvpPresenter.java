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

    void loadCategoryBanners(GetBannerRequest request);

    void loadCategoryTree();

    void loadTopBrands();

    boolean isAccessAnonymousEnabled();

    void cancelRequest();

    boolean isAuthorized();

    boolean isGoogleAdsEnabled();

    int getBannerColumnCount();

    boolean getPrefersOldShopBannerDimensions();

    void setPrefersOldShopBannerDimensions(boolean doesPrefer);
}
