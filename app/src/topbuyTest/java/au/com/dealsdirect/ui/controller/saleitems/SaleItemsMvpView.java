package au.com.dealsdirect.ui.controller.saleitems;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface SaleItemsMvpView extends MvpView{

    void onLoadSortingFacetsFinished(List<SortingResponse> responseList);

    void showSaleItems(GetSaleItemsResponse getSaleItemsResponse, boolean forFacetCorrection);

    void refresh();

    void showProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String skuId, String saleId);

    void unbindPaginate();

    void onPassFiltersData(Bundle bundle);

    void showCategoriesController();

    void onCategoryClicked(String chosenCategoryName, int color);

    String getChosenCategory();

    void onShowSelectedCategoryText();

    void onExecuteCategoryChangeApiCall(String chosenCategoryKey);

    void onShowTransparentOverlay();

    void onHideTransparentOverlay();

    void onShowSearchFilters();

    void onHideSearchFilters();

    void onShowKeyboard();
}
