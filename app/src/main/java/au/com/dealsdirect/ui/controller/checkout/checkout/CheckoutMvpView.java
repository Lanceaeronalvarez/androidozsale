package au.com.dealsdirect.ui.controller.checkout.checkout;

import java.util.List;

import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Voucher;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.main.BrainTreeListeners;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface CheckoutMvpView extends MvpView {
    void showCartDetails(List<Item> items);

    void showAddressDetails(DeliveryAddress deliveryAddress, List<DecorationInfoList> decorationInfoList);

    void showPaymentDetails(PaymentMethod paymentMethod);

    void showVoucherDetails(List<Voucher> vouchers);

    void showSummaryDetails(Summary summary);

    void setPaymentList(List<PaymentMethod> paymentList);

    void triggerLoginTicket();

     void updateCheckoutBadge();
}
