package au.com.dealsdirect.data.network.model.fcm;
/*
 * Created by CodeineBot on 1/26/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterDevice  {

    public static class RequestValue {
        public String app;
        public String deviceID;
        public String platform;
        public String token;
        public String manufacture;
        public String model;
        public String autologinTicket;
    }

    public static class ResponseValue {
        public Response getD() {
            return d;
        }

        private Response d;

        public class Response {
            @SerializedName("Result")
            @Expose
            private Boolean result;
            @SerializedName("Message")
            @Expose
            private String message;
            @SerializedName("Value")
            @Expose
            private Value value;

            public class Value {

                @SerializedName("Registered")
                @Expose
                private int registered;

                public int getRegistered() {
                    return registered;
                }
            }

            public Boolean getResult() {
                return result;
            }
            public Value getValue() {
                return value;
            }

            public String getMessage() {
                return message;
            }

        }
    }
}
