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

            public Request(String paymentType, String paymentNonce, String paymentToken, String deviceData) {
                this.paymentType = paymentType;
                this.paymentNonce = paymentNonce;
                this.paymentToken = paymentToken;
                this.deviceData = deviceData;
            }
        }
    }

    public static class ResponseValue {
        public Response getD() {
            return d;
        }

        private Response d;

        public class Response extends LegacyBaseResponseValue {
            @SerializedName("ScheduledPlan")
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
