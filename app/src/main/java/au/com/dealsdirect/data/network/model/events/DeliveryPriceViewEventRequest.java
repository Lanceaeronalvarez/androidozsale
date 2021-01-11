package au.com.dealsdirect.data.network.model.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryPriceViewEventRequest {

    public static int OPERATION_AUTO = 1;
    public static int OPERATION_BY_CLICK = 2;
    public static int OPERATION_NONE = 3;

    @SerializedName("eventType")
    @Expose
    private Integer eventType;
    @SerializedName("frontEndInfo")
    @Expose
    private FrontEndInfo frontEndInfo;
    @SerializedName("visitorInfo")
    @Expose
    private VisitorInfo visitorInfo;
    @SerializedName("deliveryPriceInfo")
    @Expose
    private DeliveryPriceInfo deliveryPriceInfo;

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public FrontEndInfo getFrontEndInfo() {
        return frontEndInfo;
    }

    public void setFrontEndInfo(FrontEndInfo frontEndInfo) {
        this.frontEndInfo = frontEndInfo;
    }

    public VisitorInfo getVisitorInfo() {
        return visitorInfo;
    }

    public void setVisitorInfo(VisitorInfo visitorInfo) {
        this.visitorInfo = visitorInfo;
    }

    public DeliveryPriceInfo getDeliveryPriceInfo() {
        return deliveryPriceInfo;
    }

    public void setDeliveryPriceInfo(DeliveryPriceInfo deliveryPriceInfo) {
        this.deliveryPriceInfo = deliveryPriceInfo;
    }

    public static class DeliveryPriceInfo {
        @SerializedName("postcode")
        @Expose
        private String postcode;
        @SerializedName("deliveryPrice")
        @Expose
        private Float deliveryPrice;
        @SerializedName("operation")
        @Expose
        private int operation;
        @SerializedName("productId")
        @Expose
        private String productId;
        @SerializedName("isShippingAvailable")
        @Expose
        private boolean isShippingAvailable;
        @SerializedName("shippingPolicyId")
        @Expose
        private String shippingPolicyId;
        @SerializedName("shippingPolicyName")
        @Expose
        private String shippingPolicyName;
        @SerializedName("supplierId")
        @Expose
        private String supplierId;
        @SerializedName("freeShipping")
        @Expose
        private Boolean freeShipping;

        public String getPostcode() {
            return postcode;
        }

        public void setPostcode(String postcode) {
            this.postcode = postcode;
        }

        public Float getDeliveryPrice() {
            return deliveryPrice;
        }

        public void setDeliveryPrice(Float deliveryPrice) {
            this.deliveryPrice = deliveryPrice;
        }

        public int getOperation() {
            return operation;
        }

        public void setOperation(int operation) {
            this.operation = operation;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public boolean isShippingAvailable() {
            return isShippingAvailable;
        }

        public void setShippingAvailable(boolean shippingAvailable) {
            isShippingAvailable = shippingAvailable;
        }

        public String getShippingPolicyId() {
            return shippingPolicyId;
        }

        public void setShippingPolicyId(String shippingPolicyId) {
            this.shippingPolicyId = shippingPolicyId;
        }

        public String getShippingPolicyName() {
            return shippingPolicyName;
        }

        public void setShippingPolicyName(String shippingPolicyName) {
            this.shippingPolicyName = shippingPolicyName;
        }

        public String getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }

        public Boolean getFreeShipping() {
            return freeShipping;
        }

        public void setFreeShipping(Boolean freeShipping) {
            this.freeShipping = freeShipping;
        }
    }
}
