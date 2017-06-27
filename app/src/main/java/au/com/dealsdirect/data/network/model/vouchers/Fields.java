
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Fields {

    @SerializedName("PhoneNumberFormat")
    @Expose
    private PhoneNumberFormat phoneNumberFormat;
    @SerializedName("VerificationCodeFormat")
    @Expose
    private VerificationCodeFormat verificationCodeFormat;

    public PhoneNumberFormat getPhoneNumberFormat() {
        return phoneNumberFormat;
    }

    public VerificationCodeFormat getVerificationCodeFormat() {
        return verificationCodeFormat;
    }
}
