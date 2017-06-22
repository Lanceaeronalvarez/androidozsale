package au.com.dealsdirect.ui.controller.saleitems;

import org.json.JSONObject;

import au.com.dealsdirect.ui.base.MvpPresenter;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface SaleItemsMvpPresenter<V extends SaleItemsMvpView> extends MvpPresenter<V> {

    void loadSaleItems(String categoryKey, JSONObject faceFilter);

    void loadProductDetails(String imageUrl, String itemId, String saleId);

}
