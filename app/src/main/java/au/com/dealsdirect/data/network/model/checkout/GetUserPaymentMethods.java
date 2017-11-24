package au.com.dealsdirect.data.network.model.checkout;
/*
 * Created by CodeineBot on 1/8/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.List;

import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.Value;

public class GetUserPaymentMethods  {

    public static class RequestValue {

    }

    public static class ResponseValue {
        public Response getD() {
            return d;
        }

        private Response d;

        public class Response extends LegacyBaseResponseValue {
            @SerializedName("ScheduledPayment")
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
    }
}
