package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.com.dealsdirect.data.network.model.address.DecorationInfoList;

public class Value {

    @SerializedName("SaleID")
    public String saleID;
    @SerializedName("Items")
    public List<Item> items = null;
    @SerializedName("ItemsCount")
    public Integer itemsCount;
    @SerializedName("Vouchers")
    public List<Voucher> vouchers = null;
    @SerializedName("Summary")
    public Summary summary;
    @SerializedName("IsAgeRestricted")
    public Boolean isAgeRestricted;
    @SerializedName("DeliveryAddress")
    public DeliveryAddress deliveryAddress;
    @SerializedName("DecorationInfoList")
    public List<DecorationInfoList> decorationInfoList = null;
    @SerializedName("DeliveryOptions")
    @Expose
    private List<DeliveryOption> deliveryOptions = null;
    @SerializedName("LastPaymentMethod")
    public String lastPaymentMethod;
    @SerializedName("ThreeDSecureRequired")
    public Boolean threeDSecureRequired;
    @SerializedName("PhoneVerification")
    public PhoneVerification phoneVerification;
    @SerializedName("PickupPointsEnabled")
    public Boolean pickupPointsEnabled;
    @SerializedName("NotificationMessage")
    private String notificationMessage;
    @SerializedName("DeliveryServicePackageDetail")
    @Expose
    private DeliveryServicePackageDetail deliveryServicePackageDetail;
    @SerializedName("IsEmpty")
    public boolean isEmpty = false;
    @SerializedName("OurPay")
    @Expose
    private GetCurrentOrderOurpay ourpay;

    public boolean isEmpty() {
        return isEmpty;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public Boolean getPickupPointsEnabled() {
        return pickupPointsEnabled;
    }

    public PhoneVerification getPhoneVerification() {
        return phoneVerification;
    }

    public Boolean getThreeDSecureRequired() {
        return threeDSecureRequired;
    }

    public String getLastPaymentMethod() {
        return lastPaymentMethod;
    }

    public List<DecorationInfoList> getDecorationInfoList() {
        return decorationInfoList;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Boolean getAgeRestricted() {
        return isAgeRestricted;
    }

    public Summary getSummary() {
        return summary;
    }

    public List<Voucher> getVouchers() {
        return vouchers;
    }

    public Integer getItemsCount() {
        return itemsCount;
    }

    public List<Item> getItems() {
        return items;
    }

    public String getSaleID() {
        return saleID;
    }

    public List<DeliveryOption> getDeliveryOptions() {
        return deliveryOptions;
    }

    public void setDeliveryOptions(List<DeliveryOption> deliveryOptions) {
        this.deliveryOptions = deliveryOptions;
    }

    public DeliveryServicePackageDetail getDeliveryServicePackageDetail() {
        return deliveryServicePackageDetail;
    }

    public GetCurrentOrderOurpay getOurpay() {
        return ourpay;
    }
}
