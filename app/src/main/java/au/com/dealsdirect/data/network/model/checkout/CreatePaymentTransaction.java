package au.com.dealsdirect.data.network.model.checkout;
/*
 * Created by CodeineBot on 1/9/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import au.com.dealsdirect.data.network.model.checkout.createpaymenttransaction.Value;

public class CreatePaymentTransaction {

    public static class RequestValue {
        public Request data;
        public String countryID;
        public String languageID;

        public RequestValue (Request data, String countryID, String languageID) {
            this.data = data;
            this.countryID = countryID;
            this.languageID = languageID;
        }

        public static class Request {
            private String paymentType;
            private String paymentNonce;
            private String paymentToken;
            private String deviceData;
            private String provider;

            private String selectedPaymentOption;

            public Request(){
            }

            public String getPaymentType() {
                return paymentType;
            }

            public void setPaymentType(String paymentType) {
                this.paymentType = paymentType;
            }

            public String getPaymentNonce() {
                return paymentNonce;
            }

            public void setPaymentNonce(String paymentNonce) {
                this.paymentNonce = paymentNonce;
            }

            public String getPaymentToken() {
                return paymentToken;
            }

            public void setPaymentToken(String paymentToken) {
                this.paymentToken = paymentToken;
            }

            public String getDeviceData() {
                return deviceData;
            }

            public void setDeviceData(String deviceData) {
                this.deviceData = deviceData;
            }

            public String getProvider() {
                return provider;
            }

            public void setProvider(String provider) {
                this.provider = provider;
            }

            public String getSelectedPaymentOption() {
                return selectedPaymentOption;
            }

            public void setSelectedPaymentOption(String selectedPaymentOption) {
                this.selectedPaymentOption = selectedPaymentOption;
            }
        }
    }

    public static class ResponseValue {
        public Response getD() {
            return d;
        }

        private Response d;

        public class Response extends LegacyBaseResponseValue {
            @SerializedName("Value")
            @Expose
            private Value value;

            public Value getValue() {
                return value;
            }
        }

        public boolean isPaid() {

            if (getD().getValue().getIsPaid()!=null){
                return getD().getResult() && getD().isAuthenticated() && getD().getValue().getIsPaid() == null ? false : getD().getValue().getIsPaid();

            }else{
                return getD().getResult() && getD().isAuthenticated() && getD().getValue().getTransactionIsPaid() == null ? false : getD().getValue().getTransactionIsPaid();
            }
        }

    }
}
