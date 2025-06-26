package au.com.dealsdirect.data.network.model.verificationnormalizephone;

import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 8/9/17.
 */

public class VerificationNormalizePhoneResponse {
    @SerializedName("IsAuthenticated")
    private Boolean isAuthenticated;

    @SerializedName("Value")
    private VerificationNormalizePhoneResponseValue value;

    @SerializedName("Result")
    private Boolean result;

    @SerializedName("Message")
    private String message;

    public Boolean getIsAuthenticated() {
        return isAuthenticated;
    }

    public void setIsAuthenticated(Boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public VerificationNormalizePhoneResponseValue getValue() {
        return value;
    }

    public void setValue(VerificationNormalizePhoneResponseValue value) {
        this.value = value;
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
