package au.com.dealsdirect.data.network.model.returns.currentreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 6/29/17.
 */

public class CurrentReturnResponseBody {

    @SerializedName("d")
    @Expose
    private CurrentReturnResponse currentReturnResponse;

    public CurrentReturnResponse getCurrentReturnResponse() {
        return currentReturnResponse;
    }

    public void setCurrentReturnResponse(CurrentReturnResponse currentReturnResponse) {
        this.currentReturnResponse = currentReturnResponse;
    }
}
