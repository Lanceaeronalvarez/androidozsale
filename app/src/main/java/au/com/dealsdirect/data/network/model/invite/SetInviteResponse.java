package au.com.dealsdirect.data.network.model.invite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

/**
 * Created by Paul on 7/2/17.
 */

public class SetInviteResponse {

    private Response d;

    public static class Response extends LegacyBaseResponseValue {
        @SerializedName("Value")
        @Expose
        public Value value;
    }


    public Response getValue() {
        return d;
    }

    public static class Value {
        @SerializedName("__type")
        @Expose
        private String type;

        public String getType() {
            return type;
        }
    }
}
