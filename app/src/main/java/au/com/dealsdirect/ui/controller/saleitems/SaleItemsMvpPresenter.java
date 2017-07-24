package au.com.dealsdirect.ui.controller.saleitems;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface SaleItemsMvpPresenter<V extends SaleItemsMvpView> extends MvpPresenter<V> {

    void loadSaleItems(String categoryKey, String saleId, String searchQuery, int pageNumber, ArrayList<SearchChipModel> chipsList);

    void loadProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String itemId, String saleId);

    void loadSortingFacets();
}
