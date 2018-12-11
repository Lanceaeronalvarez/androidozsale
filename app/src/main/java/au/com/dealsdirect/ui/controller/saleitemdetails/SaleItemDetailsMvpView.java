package au.com.dealsdirect.ui.controller.saleitemdetails;

import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 08/06/2017.
 */

public interface SaleItemDetailsMvpView extends MvpView {

    void showSaleDetails(GetSaleItemDetailsResponse saleDetail);

    void showAddToCartResponse(Value addToCartDetailsResponse);

    void showAddToCartResponseFailed();

    void showMyPayDetails(GetSaleItemDetailsResponse value, Ourpay ourpay);

    void onCallGetBasketItemsQuantity();

    int getVerticalOffset();

    void toggleClipPadding(boolean isClipped);
}
