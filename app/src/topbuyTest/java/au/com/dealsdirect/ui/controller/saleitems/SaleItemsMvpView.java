package au.com.dealsdirect.ui.controller.saleitems;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchTagsAdapter;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface SaleItemsMvpView extends MvpView{

    void onLoadSortingFacetsFinished(List<SortingResponse> responseList);

    void showSaleItems(GetSaleItemsResponse getSaleItemsResponse, boolean forFacetCorrection);

    void onLoadSaleItemsWhileTyping(String searchQuery);

    void refresh();

    void showProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String skuId, String saleId);

    void unbindPaginate();

    void onShowCategoriesController();

    void categoryClicked(int color);

    String getChosenCategory();

    void onDismissCategoriesController();

    void onExecuteCategoryChangeApiCall(String chosenCategoryKey, String chosenCategoryName);

    void onShowTransparentOverlay();

    void onHideTransparentOverlay();

    void onShowSearchFilters(String facetFilterName);

    void onHideSearchFilters();

    void onShowKeyboard();

    SearchTagsAdapter onGetSearchTagsAdapter();

    void onUpdateShopFilters();

    boolean isChangeStarted();

    boolean isDefaultBool();

    boolean isOverlayVisible();

    void onCallGetBasketItemsQuantity();

    void onCallGetCategoryTree(List<GetCategoryTreeResponse> response);

    void setCallGetCategoryTreeFinished(boolean val);

    void showNoNetworkLayout();

    void hideNoNetworkLayout();

    boolean isCallGetCategoryTreeFinished();

    boolean isCategoriesActive();

    void setIsSearchActive(boolean val);

    boolean isSearchFiltersActive();
}
