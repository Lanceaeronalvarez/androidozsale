package au.com.dealsdirect.ui.controller.shops;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface ShopsMvpPresenter<V extends ShopsMvpView> extends MvpPresenter<V> {

    void loadShopsBanner(GetBannerRequest request);

    void loadShopsBanner(GetBannerRequest request, boolean getOnlyFromNetwork);

    void loadCategoryTree();

    boolean isAccessAnonymousEnabled();

    boolean isAuthorized();

    void selectBanner(String saleId,
                      String bannerTitle,
                      String bannerId,
                      int position,
                      String imageUrl,
                      String endDate,
                      boolean isAvailable);

}
