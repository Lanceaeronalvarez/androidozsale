package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

/**
 * Created by smartwave on 19/01/2017.
 */

public class AddAndApplyVoucherByKeyResponse {

    public Response d;

    public static class Response extends LegacyBaseResponseValue {

        @SerializedName("ScheduledPayment")
        @Expose
        private Value value;
    }

    public Response getValue() {
        return d;
    }
}
