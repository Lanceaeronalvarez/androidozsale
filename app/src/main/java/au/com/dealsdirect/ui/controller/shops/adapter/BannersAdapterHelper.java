package au.com.dealsdirect.ui.controller.shops.adapter;

import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.events.BannerClickEventRequest;
import au.com.dealsdirect.service.datacollection.enums.Events;

public interface BannersAdapterHelper {
    int getBannerColumnCount();
    boolean isGoogleAdsEnabled();
    void onClickFreeDelivery(String deliveryThreshold, String deliveryType);
    void specialBannerEvent(BannerClickEventRequest bannerClickEventRequest, String saleName);
    void onBannerTapped(GetBannerResponse.Banner banner, int position, String imgUrl, Events bannerType,
                   String saleName);
}
