package au.com.dealsdirect.data.network.model.checkout;


/**
 * Created by MTC on 2019-10-10.
 */
public class CreatePaymentTransactionGPay {

    public static class RequestValue {
        public Request data;
        public String countryID;
        public String languageID;
        private String postcode;

        public RequestValue (Request data, String countryID, String languageID, String postcode) {
            this.data = data;
            this.countryID = countryID;
            this.languageID = languageID;
            this.postcode = postcode;
        }

        public static class Request {
            private String paymentType;
            private String paymentMethodId;

            public Request(String paymentType, String paymentMethodId) {
                this.paymentType = paymentType;
                this.paymentMethodId = paymentMethodId;
            }
        }

    }
}
