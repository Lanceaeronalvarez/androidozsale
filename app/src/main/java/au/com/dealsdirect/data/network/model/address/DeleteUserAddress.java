package au.com.dealsdirect.data.network.model.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

/**
 * dp Created by Admin on 3/28/17.
 */
public class DeleteUserAddress  {

    public static final class RequestValues {

        @SerializedName("addressID")
        @Expose
        private String addressID;

        public RequestValues(String addressID) {
            this.addressID = addressID;
        }

        public String getAddressID() {
            return addressID;
        }
    }


    public static final class ResponseValue{
        public Response d;

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
}
