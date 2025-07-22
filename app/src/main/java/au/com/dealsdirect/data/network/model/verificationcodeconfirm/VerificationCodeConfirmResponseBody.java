package au.com.dealsdirect.data.network.model.verificationcodeconfirm;

import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 8/9/17.
 */

public class VerificationCodeConfirmResponseBody {

    @SerializedName("d")
    private VerificationCodeConfirmResponse verificationCodeConfirmResponse;

    public VerificationCodeConfirmResponse getVerificationCodeConfirmResponse() {
        return verificationCodeConfirmResponse;
    }

    public void setVerificationCodeConfirmResponse(VerificationCodeConfirmResponse verificationCodeConfirmResponse) {
        this.verificationCodeConfirmResponse = verificationCodeConfirmResponse;
    }
}
