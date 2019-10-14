package au.com.dealsdirect.ui.controller.saleitemdetails;

import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;

/**
 * Created by smartwave on 08/06/2017.
 */

public interface SaleItemDetailsMvpView extends MvpView {

    void showSaleDetails(GetSaleItemDetailsResponse saleDetail);

    void showAddToCartResponse(CheckoutDetailsMapper addToCartDetailsResponse);

    void showAddToCartResponseFailed();

    void showMyPayDetails(GetSaleItemDetailsResponse value, Ourpay ourpay);

    void showAfterpayDetails(int installmentsCount, double installmentAmount, String currency);

    void onCallGetBasketItemsQuantity();

    int getVerticalOffset();

    void toggleClipPadding(boolean isClipped);

    void setDynamicDiscount(String discountText);

    void setIsAfterpayDetailsVisible(boolean visible);
}
