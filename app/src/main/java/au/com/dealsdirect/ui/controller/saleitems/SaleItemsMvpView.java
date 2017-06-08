package au.com.dealsdirect.ui.controller.saleitems;

import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface SaleItemsMvpView extends MvpView{

    void showSaleItems(GetPublicSaleItemsResponse getPublicSaleItemsResponse);

    void refresh();

}
