package au.com.dealsdirect.data.network.model.accountdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by smartwave on 02/08/2018.
 */

public class AccountData {

    @SerializedName("accountId")
    @Expose
    private String accountId;
    @SerializedName("facebook")
    @Expose
    private Facebook facebook;
    @SerializedName("promoBanner")
    @Expose
    private PromoBanner promoBanner;
    @SerializedName("sorting")
    @Expose
    private Sorting sorting;

    @SerializedName("afterpay")
    @Expose
    private Afterpay afterpay;
    @SerializedName("payPal")
    @Expose
    private PayPal payPal;
    @SerializedName("freeDelivery")
    @Expose
    private FreeDelivery freeDelivery;
    @SerializedName("selectDay")
    @Expose
    private SelectDay selectDay;
    @SerializedName("promoEvent")
    @Expose
    private PromoEvent promoEvent;
    @SerializedName("pricingInfo")
    @Expose
    private SupplierOriginalPriceInfo supplierOriginalPriceInfo;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Facebook getFacebook() {
        return facebook;
    }

    public void setFacebook(Facebook facebook) {
        this.facebook = facebook;
    }

    public PromoBanner getPromoBanner() {
        return promoBanner;
    }

    public void setPromoBanner(PromoBanner promoBanner) {
        this.promoBanner = promoBanner;
    }

    public Sorting getSorting() {
        return sorting;
    }

    public void setSorting(Sorting sorting) {
        this.sorting = sorting;
    }

    public Afterpay getAfterpay() {
        return afterpay;
    }

    public void setAfterpay(Afterpay afterpay) {
        this.afterpay = afterpay;
    }

    public PayPal getPayPal() {
        return payPal;
    }

    public void setPayPal(PayPal payPal) {
        this.payPal = payPal;
    }

    public FreeDelivery getFreeDelivery() {
        return freeDelivery;
    }

    public void setFreeDelivery(FreeDelivery freeDelivery) {
        this.freeDelivery = freeDelivery;
    }

    public SelectDay getSelectDay() {
        return selectDay;
    }

    public void setSelectDay(SelectDay selectDay) {
        this.selectDay = selectDay;
    }

    public PromoEvent getPromoEvent() {
        return promoEvent;
    }

    public void setPromoEvent(PromoEvent promoEvent) {
        this.promoEvent = promoEvent;
    }

    public SupplierOriginalPriceInfo getSupplierOriginalPriceInfo() {
        return supplierOriginalPriceInfo;
    }

    public static class Facebook {

        @SerializedName("appId")
        @Expose
        private String appId;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

    }

    public static class PromoBanner {

        @SerializedName("isEnabled")
        @Expose
        private Boolean isEnabled;

        public Boolean getIsEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

    }

    public static class Sorting {

        @SerializedName("isEnabled")
        @Expose
        private Boolean isEnabled;

        public Boolean getIsEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

    }

    public static class Afterpay {
        @SerializedName("isEnabled")
        @Expose
        private Boolean isEnabled;

        public Boolean isEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(Boolean enabled) {
            isEnabled = enabled;
        }
    }

    public static class PayPal {
        @SerializedName("isFreeDeliveryEnabled")
        @Expose
        private Boolean isFreeDeliveryEnabled;

        public Boolean isFreeDeliveryEnabled() {
            return isFreeDeliveryEnabled;
        }

        public void setisFreeDeliveryEnabled(Boolean enabled) {
            isFreeDeliveryEnabled = enabled;
        }
    }

    public static class FreeDelivery {
        @SerializedName("isEnabled")
        @Expose
        private Boolean isEnabled;

        public Boolean isEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(Boolean enabled) {
            isEnabled = enabled;
        }
    }

    public static class SelectDay {
        @SerializedName("startDate")
        @Expose
        private String startDate;
        @SerializedName("endDate")
        @Expose
        private String endDate;
        @SerializedName("isActive")
        @Expose
        private Boolean isActive;

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public Boolean isActive() {
            return isActive;
        }

        public void setActive(Boolean active) {
            isActive = active;
        }
    }

    public static class PromoEvent {
        @SerializedName("isEnabled")
        @Expose
        private Boolean isEnabled;
        @SerializedName("timeZoneOffset")
        @Expose
        private Integer timeZoneOffset;
        @SerializedName("startDate")
        @Expose
        private String startDate;
        @SerializedName("endDate")
        @Expose
        private String endDate;
        @SerializedName("isActive")
        @Expose
        private Boolean isActive;

        public Boolean isEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(Boolean enabled) {
            isEnabled = enabled;
        }


        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public Boolean isActive() {
            return isActive;
        }

        public void setActive(Boolean active) {
            isActive = active;
        }

        public Integer getTimeZoneOffset() {
            return timeZoneOffset;
        }

        public void setTimeZoneOffset(Integer timeZoneOffset) {
            this.timeZoneOffset = timeZoneOffset;
        }
    }

    public static class SupplierOriginalPriceInfo {
        @SerializedName("isEnabled")
        @Expose
        boolean isEnabled;

        public boolean isEnabled() {
            return isEnabled;
        }
    }
}
