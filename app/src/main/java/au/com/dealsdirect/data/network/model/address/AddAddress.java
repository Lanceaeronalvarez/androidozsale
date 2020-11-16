package au.com.dealsdirect.data.network.model.address;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

/**
 * dp Created by Admin on 11/9/16.
 */
public class AddAddress {

    public static class RequestValues {
        public JsonObject address;

        public RequestValues(JsonObject address){
            this.address = address;
        }
    }


    public static class ResponseValue {

        public Response d;

        public class Response extends LegacyBaseResponseValue {
            @SerializedName("Value")
            @Expose
            private String value;

            public String getValue() {
                return value;
            }
        }
    }


}
