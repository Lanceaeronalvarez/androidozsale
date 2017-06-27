package au.com.dealsdirect.ui.controller.saleitems;

import au.com.dealsdirect.ui.base.MvpPresenter;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface SaleItemsMvpPresenter<V extends SaleItemsMvpView> extends MvpPresenter<V> {

    void loadSaleItems(String categoryKey, String saleId);

    void loadProductDetails(String seoIdentifierId, String imageUrl, String itemId, String saleId);

}
