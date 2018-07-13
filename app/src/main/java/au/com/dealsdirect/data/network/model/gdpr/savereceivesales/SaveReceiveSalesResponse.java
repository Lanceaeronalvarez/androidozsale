package au.com.dealsdirect.data.network.model.gdpr.savereceivesales;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

/**
 * Created by smartwave on 12/07/2018.
 */

public class SaveReceiveSalesResponse extends LegacyBaseResponseValue {
    private Response d;

    public Response getResponse() {
        return d;
    }

    public static class Response extends LegacyBaseResponseValue {

        public String getType() {
            return type;
        }

        @SerializedName("__type")
        @Expose
        private String type;
    }
}
