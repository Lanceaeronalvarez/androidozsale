package au.com.dealsdirect.data.network.model.productdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPostcodeShippingPriceResponse {
    @SerializedName("price")
    @Expose
    private Float price;
    @SerializedName("shippingAvailability")
    @Expose
    private boolean shippingAvailability;
    @SerializedName("additional")
    @Expose
    private Additional additional;

    public Float getPrice() {
        return price;
    }

    public boolean isShippingAvailable() {
        return shippingAvailability;
    }

    public Additional getAdditional() {
        return additional;
    }

    public static class Additional {
        @SerializedName("showExtraPanel")
        @Expose
        private boolean showExtraPanel;
        @SerializedName("shippingPolicyName")
        @Expose
        private String shippingPolicyName;
        @SerializedName("shippingPolicyId")
        @Expose
        private String shippingPolicyId;
        @SerializedName("isShippingPolicy")
        @Expose
        private boolean isShippingPolicy;

        public boolean isShowExtraPanel() {
            return showExtraPanel;
        }

        public String getShippingPolicyName() {
            return shippingPolicyName;
        }

        public String getShippingPolicyId() {
            return shippingPolicyId;
        }

        public boolean isShippingPolicy() {
            return isShippingPolicy;
        }
    }
}
