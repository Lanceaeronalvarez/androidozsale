
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;

public class AddVoucherByKeyResponse {

    public Response d;

    public static class Response extends LegacyBaseResponseValue{

        @SerializedName("Value")
        @Expose
        private Value value;

        public Value getValue() {
            return value;
        }
    }

    public Response getD() {
        return d;
    }
}
