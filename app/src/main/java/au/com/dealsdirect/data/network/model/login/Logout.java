package au.com.dealsdirect.data.network.model.login;
/*
 * Created by CodeineBot on 1/17/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Logout {

    public static class RequestValue {

    }

    public static class ResponseValue {
        private Response d;

        public Response getD() {
            return d;
        }

        public class Response {
            @SerializedName("Result")
            @Expose
            private Boolean result;
            @SerializedName("Message")
            @Expose
            private String message;

            public Boolean getResult() {
                return result;
            }

            public String getMessage() {
                return message;
            }

        }

    }
}
