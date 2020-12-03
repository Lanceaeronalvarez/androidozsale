package au.com.dealsdirect.ui.controller.shops;
/*
 * Created by CodeineBot on 5/15/17.
 */

import java.util.List;

import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.banner.GetTopBrandsResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface ShopsMvpView extends MvpView {

    void showShopBanners(GetBannerResponse getBannerResponses, String categoryID, boolean isFromCache);

    void showSlidingBanners(GetBannerResponse getBannerResponses);

    void showSponsoredBanners(GetBannerResponse getBannerResponses);

    void showCategoryBanners(GetBannerResponse getBannerResponses);

    void storeCategories(List<GetCategoryTreeResponse> categories);

    void showTopBrands(List<GetTopBrandsResponse> topBrands);

    void unBindPaginate();

    void onBannerClicked(String saleId,
                         String bannerTitle,
                         String bannerId,
                         int position,
                         String imageUrl,
                         String endDate,
                         boolean isAvailable);

    void onBannerClicked(String categoryId);

    boolean isChangeInProgress();
}
