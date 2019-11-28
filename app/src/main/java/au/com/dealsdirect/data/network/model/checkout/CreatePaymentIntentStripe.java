package au.com.dealsdirect.data.network.model.checkout;

/**
 * Created by MTC on 2019-10-16.
 */
public class CreatePaymentIntentStripe {

    public static class RequestValue {
        public Request data;
        public String countryID;
        public String languageID;
        private String paymentType;
        private String paymentIntentId;

        public RequestValue (Request data, String countryID, String languageID) {
            this.data = data;
            this.countryID = countryID;
            this.languageID = languageID;
        }

        public static class Request {
            private String paymentType;
            private String paymentIntentId;

            public Request(String paymentType, String paymentIntentId) {
                this.paymentType = paymentType;
                this.paymentIntentId = paymentIntentId;
            }
        }

    }
}
