package au.com.dealsdirect.ui.controller.saleitems;

import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.banner.GetSaleBannerDetailsResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface SaleItemsMvpView extends MvpView {

    void onLoadSortingFacetsFinished(List<SortingResponse> responseList);

    void showSaleItems(GetSaleItemsResponse getSaleItemsResponse, int pageNumber, boolean forFacetCorrection, boolean isFromCache);

    void showWishlist(List<SaleItemProduct> wishlist, int offset);

    void updateWishlistWithAddition(String productId);

    void updateWishlistWithRemoval(String productId);

    void showSaleBannerDetails(GetSaleBannerDetailsResponse response);

    GetSaleItemsRequest createSaleItemsRequest(String categoryKey, int pageNumber, Set<SearchChipModel> chipsList);

    GetSaleItemsRequest createSaleItemsRequest(Set<String> categoryKeys, int pageNumber, Set<SearchChipModel> chipsList);

    void enableSaleItemsScroll(boolean val);

    Map<String, GetCategoryTreeResponse> getCategoryMap();

    void toggleTabSelection(int tabPos, boolean isTabActive);

    void toggleTabSelection();

    void storeBrandNames(BrandNames brandNames);

    void showLeaderboardBanner(GetBannerResponse response);

    void showPricingInfoText(String rrpText, Double totalPercentOff, Double originalPrice, String combinedPricingInfoText);
}
