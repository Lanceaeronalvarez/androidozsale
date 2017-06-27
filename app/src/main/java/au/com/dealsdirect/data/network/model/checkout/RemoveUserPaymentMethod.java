package au.com.dealsdirect.data.network.model.checkout;
/*
 * Created by CodeineBot on 3/28/17.
 */

import com.mysale.genie.utility.LegacyBaseResponseValue;

public class RemoveUserPaymentMethod {

    public static class RequestValue {
        private String paymentMethodToken;
        private String paymentMethodType;

        public RequestValue(String paymentMethodToken, String paymentMethodType) {
            this.paymentMethodToken = paymentMethodToken;
            this.paymentMethodType = paymentMethodType;
        }
    }

    public  class ResponseValue {
        public Response getD() {
            return d;
        }

        public Response d;

        public class Response extends LegacyBaseResponseValue {

        }

        public boolean isAuthenticated() {
            return getD().isAuthenticated();
        }

        public boolean getResult() {
            return getD().getResult();
        }

        public String getMessage() {
            return getD().getMessage();
        }
    }
}
