package au.com.dealsdirect.ui.controller.saleitemdetails;

import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 08/06/2017.
 */

public interface SaleItemDetailsMvpView extends MvpView {

    void showSaleDetails(GetSaleItemDetailsResponse saleDetail);

    void showAddToCartResponse(boolean val);

    void showMyPayDetails(GetSaleItemDetailsResponse value, Ourpay ourpay);

    void onCallGetBasketItemsQuantity();
}
