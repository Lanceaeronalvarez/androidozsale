package au.com.dealsdirect.data.network.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 6/27/17.
 */

public class RegisterUserResponse {

    @SerializedName("d")
    @Expose
    public Response response;

    public static class Response {

        public boolean IsAuthenticated;
        public Value Value;
        public boolean Result;
        public String Message;

    }

    public static class Value {
        public String Ticket;
        public boolean Registered;
    }

    public boolean isSuccess() {
        return response.Result;
    }

    public String getTicket() {
        return response.Value.Ticket;
    }

    public String getMessage() {
        return response.Message;
    }

}
