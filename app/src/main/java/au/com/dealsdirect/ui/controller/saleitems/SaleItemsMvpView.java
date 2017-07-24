package au.com.dealsdirect.ui.controller.saleitems;

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

    void showSaleItems(GetSaleItemsResponse getSaleItemsResponse);

    void refresh();

    void showProductDetails(RecyclerView.ViewHolder viewHolder,int position, String seoIdentifierId, String imageUrl, String itemId, String saleId);

}
