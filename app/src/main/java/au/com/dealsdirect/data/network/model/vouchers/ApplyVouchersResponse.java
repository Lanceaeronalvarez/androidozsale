
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

public class ApplyVouchersResponse {

    private Response d;

    public static class Response extends LegacyBaseResponseValue {

        @SerializedName("ScheduledPlan")
        @Expose
        public Value value;
    }

    public Response getValue() {
        return d;
    }
}
