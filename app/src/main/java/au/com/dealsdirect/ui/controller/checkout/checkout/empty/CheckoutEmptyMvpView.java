package au.com.dealsdirect.ui.controller.checkout.checkout.empty;

import java.util.List;

import au.com.dealsdirect.data.network.model.productdetails.GetBestSellerResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface CheckoutEmptyMvpView extends MvpView {

    void showCart();

    void showBestSellers(List<GetBestSellerResponse> getBestSellerResponses);

    void showPricingInfoText(String rrpText, Double totalPercentOff, Double originalPrice, String combinedPricingInfoText);

    void showRecentlyViewedItems(List<RecentlyViewedItemResponse> response);}
