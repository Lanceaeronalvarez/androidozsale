package au.com.dealsdirect.ui.controller.shops;
/*
 * Created by CodeineBot on 5/15/17.
 */

import java.util.List;

import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface ShopsMvpView extends MvpView {

    void showShopBanners(List<GetBannerResponse> getBannerResponses);

}
