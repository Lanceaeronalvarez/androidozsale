package au.com.dealsdirect.data.network.model.ourpayverificationcodeconfirm;

import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 8/9/17.
 */

public class VerificationCodeConfirmResponseValue {


    @SerializedName("ErrorMessage")
    private String errorMessage;


    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
