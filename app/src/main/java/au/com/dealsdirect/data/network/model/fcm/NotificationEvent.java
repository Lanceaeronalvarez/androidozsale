package au.com.dealsdirect.data.network.model.fcm;
/*
 * Created by CodeineBot on 1/26/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationEvent {

    public static class RequestValue {
        public String eventName;
        public String messageID;
        public String autologinTicket;
    }

    public static class ResponseValue {
        public Response d;

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
