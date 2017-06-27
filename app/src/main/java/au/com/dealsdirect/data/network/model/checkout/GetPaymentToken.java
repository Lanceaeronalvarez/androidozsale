package au.com.dealsdirect.data.network.model.checkout;
/*
 * Created by CodeineBot on 1/9/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

public class GetPaymentToken {

    public static class RequestValue {
        private String languageID;
        private String countryID;

        public RequestValue(String languageID, String countryID) {
            this.languageID = languageID;
            this.countryID = countryID;
        }
    }

    public static class ResponseValue {

        public Response getD() {
            return d;
        }

        private Response d;

        public class Response extends LegacyBaseResponseValue {
            public ResponseValue.Value getValue() {
                return Value;
            }

            public Value Value;
        }

        public class Value {

            @SerializedName("PaymentType")
            @Expose
            private String paymentType;
            @SerializedName("Token")
            @Expose
            private String token;

            String getPaymentType() {
                return paymentType;
            }

            String getToken() {
                return token;
            }
        }

        public String getPaymentToken() {
            return getD().getValue().getToken();
        }

        public String getPaymentType() {
            return getD().getValue().getPaymentType();
        }

        public boolean isAuthenticated() {
            return getD().isAuthenticated();
        }

        public boolean isResult() {
            return getD().getResult();
        }

        public String getMessage() {
            return getD().getMessage();
        }
    }

}
