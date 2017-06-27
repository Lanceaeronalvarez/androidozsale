
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

public class AddVoucherByKeyResponse {

    public Response d;

    public static class Response extends LegacyBaseResponseValue{

        @SerializedName("Value")
        @Expose
        private Value value;
    }

    public Response getValue() {
        return d;
    }
}
