package au.com.dealsdirect.data.network.model.preferencecenter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateEmailSubscriptionResponse {

    @SerializedName("is_success")
    @Expose
    private boolean is_success;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;


    public boolean getIsSuccess() {
        return is_success;
    }
    public String getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }

}
