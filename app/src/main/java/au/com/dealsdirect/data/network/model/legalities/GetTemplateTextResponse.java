package au.com.dealsdirect.data.network.model.legalities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

/**
 * Created by Paul on 7/14/17.
 */

public class GetTemplateTextResponse {

    private Response d;

    public Response getResponse() {
        return d;
    }

    public class Response {
        @SerializedName("Value")
        @Expose
        private String value;

        public String getValue() {
            return value;
        }
    }
}
