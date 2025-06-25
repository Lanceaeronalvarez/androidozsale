package au.com.dealsdirect.ui.controller.checkout.checkout;

import com.bluelinelabs.conductor.Router;

import java.util.List;

import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.productdetails.GetBestSellerResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.data.network.model.vouchers.Voucher;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper.MappedShipment;

public interface CheckoutMvpView extends MvpView {

    String TAG = "CheckoutController";

    void loadCart();

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

    Router getDisplayRouter();

//    void initializeVisaCheckout();

    void setIsShipmentAvailable(boolean isShipmentAvailable);

    void showAgeRestriction(boolean hasAgeRestriction);

    void updateCartWithMappedValues(CheckoutDetailsMapper mappedValues);

    void showBestSellers(List<GetBestSellerResponse> getBestSellerResponses);

    void showPricingInfoText(String rrpText, Double totalPercentOff, Double originalPrice, String combinedPricingInfoText);

    void showRecentlyViewedItems(List<RecentlyViewedItemResponse> response);
}
