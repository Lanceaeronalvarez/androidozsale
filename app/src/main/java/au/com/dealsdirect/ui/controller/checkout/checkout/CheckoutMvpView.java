package au.com.dealsdirect.ui.controller.checkout.checkout;

import com.bluelinelabs.conductor.Router;

import java.util.List;

import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.vouchers.Voucher;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper.MappedShipment;

public interface CheckoutMvpView extends MvpView {

    String TAG = "CheckoutController";

    void loadCart();

    void showMyPayDetails(CheckoutDetailsMapper mappedValues, Ourpay ourpay);

    void showCartDetails(List<MappedShipment> items);

    void showCartDetailsOnChild(List<MappedShipment> items);

    void showCartDetailsOnHost(List<MappedShipment> items);

    void showCartDetailsFooter(boolean show);

    void showCartDetailsPostcode(String postcode);

    void showAddressDetails(DeliveryAddress deliveryAddress, List<DecorationInfoList> decorationInfoList);

    void showDeliveryOptions(List<DeliveryOption> deliveryOptions, DeliveryServicePackageDetail deliveryServicePackageDetail);

    void showPaymentDetails(PaymentMethod paymentMethod);

    void showVoucherDetails(List<Voucher> vouchers);

    void showSummaryDetails(Summary summary);

    void setPaymentList(List<PaymentMethod> paymentList);

    void showAfterpayPanel(boolean isAvailable, String description);

    void hideAfterpayPanel();

    void showLPayPanel();

    void hideLPayPanel();

    void showKlarnaPanel(String description);

    void hideKlarnaPanel();

    void showZipPayPanel();

    void hideZipPayPanel();

    void storeCartDetails(CheckoutDetailsMapper mappedValues);

    void triggerLoginTicket();

    void updateCheckoutBadge();

    boolean isCartLoading();

    void setCartIsLoading(boolean val);

    CheckoutMvpPresenter getPresenter();

    boolean isOurPaySelectDeliveryMethod();

    void setIsPaymentMethodChanged(boolean isPaymentMethodChanged);

    Router getDisplayRouter();

//    void initializeVisaCheckout();

    void setIsShipmentAvailable(boolean isShipmentAvailable);

    void showAgeRestriction(boolean hasAgeRestriction);

    void updateCartWithValue(Value value);

    void updateCartWithMappedValues(CheckoutDetailsMapper mappedValues);
}
