package au.com.dealsdirect.ui.controller.search;
/*
 * Created by DP on 6/19/17.
 */


import android.support.v7.widget.RecyclerView;

import au.com.dealsdirect.ui.base.MvpPresenter;

public interface SearchMvpPresenter<V extends SearchMvpView> extends MvpPresenter<V> {

    void loadSaleItems(String categoryKey, String saleId, String searchQuery, int pageNumber);

    void loadProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String itemId, String saleId);
}
