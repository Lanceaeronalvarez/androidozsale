package au.com.dealsdirect.data.network.model.openpay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateOpenpayOrderResponse {

    @SerializedName("d")
    @Expose
    public d d;

    public class d {
        @SerializedName("IsAuthenticated")
        @Expose
        private boolean isAuthenticated;
        @SerializedName("Value")
        @Expose
        private Value value;
        @SerializedName("Result")
        @Expose
        private boolean result;
        @SerializedName("Message")
        @Expose
        private String message;

        public boolean isAuthenticated() {
            return isAuthenticated;
        }

        public Value getValue() {
            return value;
        }

        public boolean getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }
    }

    public class Value {

        @SerializedName("planId")
        @Expose
        private String planId;
        @SerializedName("orderId")
        @Expose
        private String orderId;
        @SerializedName("postUrl")
        @Expose
        private String postUrl;
        @SerializedName("expires")
        @Expose
        private String expirationDate;
        @SerializedName("isSuccess")
        @Expose
        private boolean isSuccess;
        @SerializedName("error")
        @Expose
        String error;
        @SerializedName("paymentType")
        @Expose
        private int paymentType;

        public String getPlanId() {
            return planId;
        }

        public void setPlanId(String planId) {
            this.planId = planId;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getPostUrl() {
            return postUrl;
        }

        public void setPostUrl(String postUrl) {
            this.postUrl = postUrl;
        }

        public String getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(String expirationDate) {
            this.expirationDate = expirationDate;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public void setSuccess(boolean success) {
            isSuccess = success;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public int getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(int paymentType) {
            this.paymentType = paymentType;
        }
    }
}
