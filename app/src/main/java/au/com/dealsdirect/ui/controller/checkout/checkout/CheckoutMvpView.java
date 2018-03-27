package au.com.dealsdirect.ui.controller.checkout.checkout;

import java.util.List;

import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Voucher;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyResponse;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface CheckoutMvpView extends MvpView {

    void showMyPayDetails(Value value, Ourpay ourpay);

    void showCartDetails(List<Item> items);

    void showAddressDetails(DeliveryAddress deliveryAddress, List<DecorationInfoList> decorationInfoList);

    void showPaymentDetails(PaymentMethod paymentMethod);

    void showVoucherDetails(List<Voucher> vouchers);

    void showSummaryDetails(Summary summary);

    void setPaymentList(List<PaymentMethod> paymentList);

    void storeCartDetails(Value value);

    void triggerLoginTicket();

    void updateCheckoutBadge();

    boolean isCartLoading();

    void setCartIsLoading(boolean val);

    boolean isViewPagerOnCheckout();

    void onAddAndAppliedVoucher(AddAndApplyVoucherByKeyResponse response);

}
