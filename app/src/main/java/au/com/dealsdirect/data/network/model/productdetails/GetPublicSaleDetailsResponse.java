package au.com.dealsdirect.data.network.model.productdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by smartwave on 09/06/2017.
 */

public class GetPublicSaleDetailsResponse {
    public D getD() {
        return d;
    }

    @SerializedName("d")
    @Expose
    public D d;

    public static class D {

        @SerializedName("ScheduledPlan")
        @Expose
        public Value value;
        @SerializedName("Result")
        @Expose
        public Boolean result;
        @SerializedName("Message")
        @Expose
        public String message;

        public Value getValue() {
            return value;
        }

        public Boolean getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class Value {

        @SerializedName("Shipping")
        @Expose
        public String shipping;
        @SerializedName("Returns")
        @Expose
        public Object returns;
        @SerializedName("Pricing")
        @Expose
        public String pricing;

        public String getShipping() {
            return shipping;
        }

        public Object getReturns() {
            return returns;
        }

        public String getPricing() {
            return pricing;
        }
    }
}
