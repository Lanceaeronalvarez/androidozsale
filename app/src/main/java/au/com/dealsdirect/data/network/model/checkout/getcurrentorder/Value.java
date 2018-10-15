package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.com.dealsdirect.data.network.model.address.DecorationInfoList;

public class Value {

    @SerializedName(value = "SaleID", alternate = {"saleID"})
    public String saleID;
    @SerializedName(value = "Items", alternate = {"items"})
    public List<Item> items = null;
    @SerializedName(value = "ItemsCount", alternate = {"itemsCount"})
    public Integer itemsCount;
    @SerializedName(value = "Vouchers", alternate = {"vouchers"})
    public List<Voucher> vouchers = null;
    @SerializedName(value = "Summary", alternate = {"summary"})
    public Summary summary;
    @SerializedName(value = "IsAgeRestricted", alternate = {"isAgeRestricted"})
    public Boolean isAgeRestricted;
    @SerializedName(value = "DeliveryAddress", alternate = {"deliveryAddress"})
    public DeliveryAddress deliveryAddress;
    @SerializedName(value = "DecorationInfoList", alternate = {"decorationInfoList"})
    public List<DecorationInfoList> decorationInfoList = null;
    @SerializedName(value = "DeliveryOptions", alternate = {"deliveryOptions"})
    @Expose
    private List<DeliveryOption> deliveryOptions = null;
    @SerializedName(value = "LastPaymentMethod", alternate = {"lastPaymentMethod"})
    public String lastPaymentMethod;
    @SerializedName(value = "ThreeDSecureRequired", alternate = {"threeDSecureRequired"})
    public Boolean threeDSecureRequired;
    @SerializedName(value = "PhoneVerification", alternate = {"phoneVerification"})
    public PhoneVerification phoneVerification;
    @SerializedName(value = "PickupPointsEnabled", alternate = {"pickupPointsEnabled"})
    public Boolean pickupPointsEnabled;
    @SerializedName(value = "NotificationMessage", alternate = {"notificationMessage"})
    private String notificationMessage;
    @SerializedName(value = "DeliveryServicePackageDetail", alternate = {"deliveryServicePackageDetail"})
    @Expose
    private DeliveryServicePackageDetail deliveryServicePackageDetail;
    @SerializedName(value = "IsEmpty", alternate = {"isEmpty"})
    public boolean isEmpty = false;
    @SerializedName(value = "OurPay", alternate = {"ourPay"})
    @Expose
    private GetCurrentOrderOurpay ourpay;
    @SerializedName(value = "OurPaySelect", alternate = {"ourPaySelect"})
    @Expose
    private GetOurPaySelect ourPaySelect;

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

    public GetOurPaySelect getOurPaySelect() {
        return ourPaySelect;
    }

    public int getOurPaySelectTermsAndConditions() {
        return ourPaySelect.getTermsAndConditions();
    }

    private static class GetOurPaySelect {
        @SerializedName(value = "TermsAndConditions", alternate = {"termsAndConditions"})
        @Expose
        private int termsAndConditions;

        private int getTermsAndConditions() {
            return termsAndConditions;
        }
    }
}
