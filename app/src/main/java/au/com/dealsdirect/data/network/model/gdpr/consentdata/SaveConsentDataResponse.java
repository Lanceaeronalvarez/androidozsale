package au.com.dealsdirect.data.network.model.gdpr.consentdata;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;


public class SaveConsentDataResponse {

    public Response getD() {
        return d;
    }

    private Response d;

    public class Response extends LegacyBaseResponseValue {

        @SerializedName("__type")
        @Expose
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
