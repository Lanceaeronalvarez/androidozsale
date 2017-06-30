package au.com.dealsdirect.data.network.model.returns.currentreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 6/29/17.
 */

public class CurrentReturnResponse {

    @SerializedName("IsAuthenticated")
    @Expose
    private Boolean isAuthenticated;
    @SerializedName("List")
    @Expose
    private java.util.List<CurrentReturns> currentReturns = null;
    @SerializedName("Result")
    @Expose
    private Boolean result;
    @SerializedName("Message")
    @Expose
    private String message;

    public Boolean getIsAuthenticated() {
        return isAuthenticated;
    }

    public void setIsAuthenticated(Boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public java.util.List<CurrentReturns> getCurrentReturns() {
        return currentReturns;
    }

    public void setCurrentReturns(java.util.List<CurrentReturns> currentReturns) {
        this.currentReturns = currentReturns;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
