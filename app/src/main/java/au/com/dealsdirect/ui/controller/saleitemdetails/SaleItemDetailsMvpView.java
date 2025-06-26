package au.com.dealsdirect.ui.controller.saleitemdetails;

import java.util.List;

import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetPostcodeShippingPriceResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetYouMayAlsoLikeResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecommendedItemsResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.SaleItemDetails;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;

/**
 * Created by smartwave on 08/06/2017.
 */

public interface SaleItemDetailsMvpView extends MvpView {

    void showProductDetails(SaleItemDetails saleDetail);

    void showAddToCartResponse(CheckoutDetailsMapper addToCartDetailsResponse);

    void showAddToCartResponseFailed();

    void showAfterpayDetails(int installmentsCount, double installmentAmount, String currency);

    void onCallGetBasketItemsQuantity();

    void setDynamicDiscount(String discountText);

    void setIsAfterpayDetailsVisible(boolean visible);

    void showRecommendedItems(List<RecommendedItemsResponse> recommendedItemsResponseList);

    void showYouMayAlsoLike(List<GetYouMayAlsoLikeResponse> response);

    void showRecentlyViewedItems(List<RecentlyViewedItemResponse> response);

    void showFreeShipping(String deliveryType, String deliveryThreshold);

    void showPostcodeForm(boolean show);

    void showDefaultPostcode(String postcode);

    void showPreviewShippingPrice(GetPostcodeShippingPriceResponse response, String postcode, Integer operation);

    void showLeaderboardBanner(GetBannerResponse response);

    void showPricingInfoText(String rrpText, Double totalPercentOff, Double originalPrice, String combinedPricingInfoText);

    void showTrendingBrands(GetBannerResponse getBannerResponses);
}
