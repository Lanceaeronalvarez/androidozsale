package au.com.dealsdirect.data.network.model.checkout;
/*
 * Created by CodeineBot on 1/9/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.List;

import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.Value;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;

public class CreatePaymentMethod {

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
            private String deviceData;

            public Request(String paymentType, String paymentNonce, String deviceData) {
                this.paymentType = paymentType;
                this.paymentNonce = paymentNonce;
                this.deviceData = deviceData;
            }
        }
    }

    public static class ResponseValue {
        public Response getD() {
            return d;
        }

        private Response d;

        public class Response extends LegacyBaseResponseValue{
            @SerializedName("ScheduledPlan")
            @Expose
            private Value value;

            public Value getValue() {
                return value;
            }
        }

        public List<PaymentMethod> getUserPaymentMethods() {
            return getD().getValue().getPaymentMethods();
        }

        public String getLastPaidToken() {
            return getD().getValue().getLastPaidToken();
        }

        public boolean getResult() {
            return getD().getResult();
        }

        public boolean getIsAuthenticated() {
            return getD().isAuthenticated();
        }

        public String getMessage() {
            return getD().getMessage();
        }

    }

}
