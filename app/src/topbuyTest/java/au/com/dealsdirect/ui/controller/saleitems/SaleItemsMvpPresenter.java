package au.com.dealsdirect.ui.controller.saleitems;

import android.support.v7.widget.RecyclerView;

import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchTagsAdapter;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface SaleItemsMvpPresenter<V extends SaleItemsMvpView> extends MvpPresenter<V> {

    void loadSaleItems(GetSaleItemsRequest getSaleItemsRequest);

    void loadSaleItemsWhileTyping(String searchQuery);

    void loadProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String skuId, String saleId);

    void loadSortingFacets();

    void showCategoriesController();

    void categoryClicked(String chosenCategoryName, int color);

    void dismissCategoriesController();

    void executeCategoryChangeApiCall(String chosenCategoryKey);

    void showKeyboard();

    void showSearchFilters(String facetFilterName);

    void hideSearchFilters();

    void showTransparentOverlay();

    void hideTransparentOverlay();

    SearchTagsAdapter getSearchTagsAdapter();

    void updateShopFilters();

    boolean isAuthorized();

    void callGetBasketItemsQuantity();

    void callGetCategoryTree();

    boolean isCallGetCategoryTreeFinished();

}
