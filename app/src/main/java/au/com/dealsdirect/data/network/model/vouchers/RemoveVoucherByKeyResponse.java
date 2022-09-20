package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;

/**
 * Created by smartwave on 19/01/2017.
 */

public class RemoveVoucherByKeyResponse {

    public Response d;

    public static class Response extends LegacyBaseResponseValue {

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
