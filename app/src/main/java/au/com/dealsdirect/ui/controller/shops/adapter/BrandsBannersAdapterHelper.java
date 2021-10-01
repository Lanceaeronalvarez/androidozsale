package au.com.dealsdirect.ui.controller.shops.adapter;

import au.com.dealsdirect.data.network.model.banner.GetTopBrandsResponse;

public interface BrandsBannersAdapterHelper {
    int getBannerColumnCount();

    void onBannerClick(GetTopBrandsResponse brand);

    void onInfoClick(String title, String description);
}
