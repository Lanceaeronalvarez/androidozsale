package au.com.dealsdirect.ui.controller.shops;
/*
 * Created by CodeineBot on 5/15/17.
 */

import au.com.dealsdirect.data.network.model.banner.GetPublicSalesBannerResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface ShopsMvpView extends MvpView {

    void showShopBanners(GetPublicSalesBannerResponse getPublicSalesBannerResponse);

}
