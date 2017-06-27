
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Value {

    @SerializedName("Items")
    @Expose
    private List<Item> items = null;
    @SerializedName("Vouchers")
    @Expose
    private List<Voucher> vouchers = null;
    @SerializedName("Summary")
    @Expose
    private Summary summary;
    @SerializedName("DeliveryAddress")
    @Expose
    private DeliveryAddress deliveryAddress;
    @SerializedName("DecorationInfoList")
    @Expose
    private List<DecorationInfoList> decorationInfoList = null;
    @SerializedName("MyPayDetails")
    @Expose
    private MyPayDetails myPayDetails;
    @SerializedName("ThreeDSecureRequired")
    @Expose
    private Boolean threeDSecureRequired;
    @SerializedName("PhoneVerification")
    @Expose
    private PhoneVerification phoneVerification;
    @SerializedName("PickupPointsEnabled")
    @Expose
    private Boolean pickupPointsEnabled;
    @SerializedName("NotificationMessage")
    @Expose
    private Object notificationMessage;

    public List<Item> getItems() {
        return items;
    }

    public List<Voucher> getVouchers() {
        return vouchers;
    }

    public Summary getSummary() {
        return summary;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public List<DecorationInfoList> getDecorationInfoList() {
        return decorationInfoList;
    }

    public MyPayDetails getMyPayDetails() {
        return myPayDetails;
    }

    public Boolean getThreeDSecureRequired() {
        return threeDSecureRequired;
    }

    public PhoneVerification getPhoneVerification() {
        return phoneVerification;
    }

    public Boolean getPickupPointsEnabled() {
        return pickupPointsEnabled;
    }

    public Object getNotificationMessage() {
        return notificationMessage;
    }
}
