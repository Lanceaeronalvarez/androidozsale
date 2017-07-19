package au.com.dealsdirect.ui.controller.search;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.support.v7.widget.RecyclerView;

import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface SearchMvpView extends MvpView {

    void showSaleItems(GetSaleItemsResponse getSaleItemsResponse);

    void refresh();

    void showProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String itemId, String saleId);


}
