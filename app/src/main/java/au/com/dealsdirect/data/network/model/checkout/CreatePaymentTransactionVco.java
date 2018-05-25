package au.com.dealsdirect.data.network.model.checkout;
/*
 * Created by CodeineBot on 1/9/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import au.com.dealsdirect.data.network.model.checkout.createpaymenttransaction.Value;

public class CreatePaymentTransactionVco {

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
            private String callId;
            private String encKey;
            private String encData;

            public Request(String paymentType, String callId, String encKey, String encData) {
                this.paymentType = paymentType;
                this.callId = callId;
                this.encKey = encKey;
                this.encData = encData;
            }
        }
    }
}
