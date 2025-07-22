package au.com.dealsdirect.data.network.model.verificationnormalizephone;

import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 8/9/17.
 */

public class VerificationNormalizePhoneResponseBody {

    @SerializedName("d")
    private VerificationNormalizePhoneResponse verificationNormalizePhoneResponse;

    public VerificationNormalizePhoneResponse getVerificationNormalizePhoneResponse() {
        return verificationNormalizePhoneResponse;
    }

    public void setD(VerificationNormalizePhoneResponse verificationNormalizePhoneResponse) {
        this.verificationNormalizePhoneResponse = verificationNormalizePhoneResponse;
    }
}
