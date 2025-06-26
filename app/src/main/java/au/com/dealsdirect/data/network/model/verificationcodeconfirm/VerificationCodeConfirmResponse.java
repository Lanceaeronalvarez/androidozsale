package au.com.dealsdirect.data.network.model.verificationcodeconfirm;

import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 8/9/17.
 */

public class VerificationCodeConfirmResponse {
    @SerializedName("IsAuthenticated")
    private Boolean isAuthenticated;

    @SerializedName("Value")
    private VerificationCodeConfirmResponseValue value;

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

    public VerificationCodeConfirmResponseValue getValue() {
        return value;
    }

    public void setValue(VerificationCodeConfirmResponseValue value) {
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
