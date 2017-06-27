
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhoneVerification {

    @SerializedName("IsRequired")
    @Expose
    private Boolean isRequired;
    @SerializedName("Fields")
    @Expose
    private Fields fields;

    public Boolean getRequired() {
        return isRequired;
    }

    public Fields getFields() {
        return fields;
    }
}
