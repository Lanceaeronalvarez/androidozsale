package au.com.dealsdirect.data.network.model.afterpay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateAfterpayOrderResponse {

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

        public boolean isAuthenticated() {
            return isAuthenticated;
        }

        public Value getValue() {
            return value;
        }
    }

    public class Value {


        @SerializedName("token")
        @Expose
        private String token;

        @SerializedName("expires")
        @Expose
        private String date;

        @SerializedName("isSuccess")
        @Expose
        private boolean isSuccess;

        @SerializedName("error")
        @Expose
        private String error;

        @SerializedName("paymentType")
        @Expose
        private String paymentType;

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public void setSuccess(boolean success) {
            isSuccess = success;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

    }
}
