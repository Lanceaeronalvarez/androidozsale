package au.com.dealsdirect.ui.controller.saleitemdetails;

import java.util.List;

import au.com.dealsdirect.data.network.model.productdetails.GetPostcodeShippingPriceResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetYouMayAlsoLikeResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecommendedItemsResponse;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;

/**
 * Created by smartwave on 08/06/2017.
 */

public interface SaleItemDetailsMvpView extends MvpView {

    void showSaleDetails(GetSaleItemDetailsResponse saleDetail);

    void showAddToCartResponse(CheckoutDetailsMapper addToCartDetailsResponse);

    void showAddToCartResponseFailed();

    void showMyPayDetails(GetSaleItemDetailsResponse value, Ourpay ourpay);

    void showAfterpayDetails(int installmentsCount, double installmentAmount, String currency);

    void onCallGetBasketItemsQuantity();

    void setDynamicDiscount(String discountText);

    void setPercentOffText(String percentOffText);

    void setIsAfterpayDetailsVisible(boolean visible);

    void showRecommendedItems(List<RecommendedItemsResponse> recommendedItemsResponseList);

    void showYouMayAlsoLike(List<GetYouMayAlsoLikeResponse> response);

    void showRecentlyViewedItems(List<RecentlyViewedItemResponse> response);

    void showFreeShipping(String deliveryType, String deliveryThreshold);

    void showPostcodeForm(boolean show);

    void showDefaultPostcode(String postcode);

    void showPreviewShippingPrice(GetPostcodeShippingPriceResponse response, String postcode, Integer operation);
}
