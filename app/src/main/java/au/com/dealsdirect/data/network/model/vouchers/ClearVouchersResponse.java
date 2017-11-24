package au.com.dealsdirect.data.network.model.vouchers;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

/**
 * Created by Admin on 3/6/17.
 */
public class ClearVouchersResponse {

    private Response d;

    public static class Response extends LegacyBaseResponseValue {

        @SerializedName("ScheduledPayment")
        @Expose
        private Object value;
        public Object getValue() {
            return value;
        }

    }

    private Response getValue() {
        return d;
    }
}
