package au.com.dealsdirect.ui.controller.saleitems;

import android.support.v7.widget.RecyclerView;

import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface SaleItemsMvpPresenter<V extends SaleItemsMvpView> extends MvpPresenter<V> {

    void loadSaleItems(GetSaleItemsRequest getSaleItemsRequest);

    void loadProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String skuId, String saleId);

    void loadSortingFacets();

    void onCategoryChanged(boolean isCategoryChanged);
}
