package au.com.dealsdirect.data.network.model.lpay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateLPayOrderResponse {
    @SerializedName("d")
    @Expose
    private D d;

    public D getD() {
        return d;
    }

    public static class D {
        @SerializedName("IsAuthenticated")
        @Expose
        private Boolean isAuthenticated;
        @SerializedName("Result")
        @Expose
        private Boolean result;
        @SerializedName("Message")
        @Expose
        private String message;
        @SerializedName("Value")
        @Expose
        private Value value;

        public Boolean getAuthenticated() {
            return isAuthenticated;
        }

        public Boolean getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }

        public Value getValue() {
            return value;
        }

        public static class Value {
            @SerializedName("PaymentUrl")
            @Expose
            private String paymentUrl;

            public String getPaymentUrl() {
                return paymentUrl;
            }
        }
    }
}
