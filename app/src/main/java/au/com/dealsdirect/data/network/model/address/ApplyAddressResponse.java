package au.com.dealsdirect.data.network.model.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;

/**
 * Created by smartwave on 07/01/2017.
 */

public class ApplyAddressResponse {

    @SerializedName("d")
    @Expose
    private Response d;

    public Response getD() {
        return d;
    }

    public void setD(Response d) {
        this.d = d;
    }

    public static class Response extends LegacyBaseResponseValue {
        @SerializedName("Value")
        @Expose
        private Value value;

        public Value getValue() {
            return value;
        }

        public void setValue(Value value) {
            this.value = value;
        }
    }
}
