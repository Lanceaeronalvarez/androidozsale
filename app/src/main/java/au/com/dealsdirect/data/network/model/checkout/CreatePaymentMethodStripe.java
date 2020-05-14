package au.com.dealsdirect.data.network.model.checkout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.List;

import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.Value;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;

/**
 * Created by MTC on 2020-01-10.
 */
public class CreatePaymentMethodStripe {

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
            public String paymentType;
            public String paymentMethodId;

            public Request (String paymentType, String paymentMethodId) {
                this.paymentType = paymentType;
                this.paymentMethodId = paymentMethodId;
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
