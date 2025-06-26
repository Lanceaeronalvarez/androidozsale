package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.vouchers.Voucher;

public class Value {

    @SerializedName(value = "SaleID", alternate = {"saleID"})
    public String saleID;
    @SerializedName(value = "Items", alternate = {"items"})
    public List<Item> items = null;
    @SerializedName(value = "Shipments", alternate = {"shipments"})
    public List<Shipment> shipments = null;
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
    @SerializedName(value = "AvailablePaymentOptions", alternate = {"availablePaymentOptions"})
    @Expose
    private List<PaymentOption> availablePaymentOptions = null;
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
    @SerializedName(value = "Afterpay", alternate = {"afterpay", "AfterPay"})
    @Expose
    private GetCurrentOrderAfterpay afterpay;
    @SerializedName("PromoCodeList")
    @Expose
    private List<PromoCode> promoCodeList;

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

    public Boolean isAgeRestricted() {
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

    public List<Shipment> getShipments() {
        return shipments;
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

    public List<PaymentOption> getAvailablePaymentOptions() {
        return availablePaymentOptions;
    }

    public void setAvailablePaymentOptions(List<PaymentOption> availablePaymentOptions) {
        this.availablePaymentOptions = availablePaymentOptions;
    }

    public DeliveryServicePackageDetail getDeliveryServicePackageDetail() {
        return deliveryServicePackageDetail;
    }

    public GetCurrentOrderAfterpay getAfterpay() {
        return afterpay;
    }

    public void setAfterpay(GetCurrentOrderAfterpay afterpay) {
        this.afterpay = afterpay;
    }

    public List<PromoCode> getPromoCodeList() {
        return promoCodeList;
    }

    public void setPromoCodeList(List<PromoCode> promoCodeList) {
        this.promoCodeList = promoCodeList;
    }

    public static class GetCurrentOrderAfterpay {
        /*
           "isAvailable" is an indicator if Afterpay is available for the price of the cart.

           "isAvailableMobileApp" dictates if Afterpay is visible and accessible at all for the user.
         */

        @SerializedName(value = "IsAvailable", alternate = {"isAvailable"})
        @Expose
        private boolean isAvailable;
        @SerializedName(value = "IsAvailableMobileApp", alternate = "isAvailableMobileApp")
        @Expose
        private boolean isAvailableMobileApp;
        @SerializedName(value = "Description", alternate = {"description"})
        @Expose
        private String description;

        public boolean isAvailable() {
            return isAvailable;
        }

        public void setAvailable(boolean available) {
            isAvailable = available;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isAvailableMobileApp() {
            return isAvailableMobileApp;
        }

        public void setAvailableMobileApp(boolean availableMobileApp) {
            isAvailableMobileApp = availableMobileApp;
        }
    }

    public static class PaymentOption {
        @SerializedName(value = "Name", alternate = {"name"})
        @Expose
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class PromoCode {
        @SerializedName("ID")
        @Expose
        private String id;
        @SerializedName("Code")
        @Expose
        private String code;
        @SerializedName("Amount")
        @Expose
        private float amount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }
    }
}
