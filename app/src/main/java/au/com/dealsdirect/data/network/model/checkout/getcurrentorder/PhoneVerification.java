package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.SerializedName;

public class PhoneVerification {
    @SerializedName("IsRequired")
    public Boolean isRequired;
    @SerializedName("Fields")
    public Fields fields;
}
