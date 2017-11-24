
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

public class AddVoucherByKeyResponse {

    public Response d;

    public static class Response extends LegacyBaseResponseValue{

        @SerializedName("ScheduledPayment")
        @Expose
        private Value value;

        public Value getValue() {
            return value;
        }
    }

    public Response getResponseValue() {
        return d;
    }
}
