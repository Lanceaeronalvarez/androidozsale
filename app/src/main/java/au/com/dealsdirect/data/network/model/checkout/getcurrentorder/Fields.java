package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.SerializedName;

public class Fields {
    @SerializedName("PhoneNumberFormat")
    public PhoneNumberFormat phoneNumberFormat;
    @SerializedName("VerificationCodeFormat")
    public VerificationCodeFormat verificationCodeFormat;
}
