package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.SerializedName;

public class VerificationCodeFormat {
    @SerializedName("Label")
    public String label;
    @SerializedName("Name")
    public String name;
    @SerializedName("Type")
    public String type;
    @SerializedName("DataType")
    public String dataType;
    @SerializedName("Class")
    public String _class;
    @SerializedName("ScheduledPlan")
    public Object value;
    @SerializedName("MaxLength")
    public Integer maxLength;
    @SerializedName("MinLength")
    public Integer minLength;
    @SerializedName("Validate")
    public String validate;
    @SerializedName("ReadOnly")
    public Boolean readOnly;
    @SerializedName("WithoutComma")
    public Boolean withoutComma;
    @SerializedName("Options")
    public Object options;
    @SerializedName("Regexp")
    public String regexp;
    @SerializedName("ValidateConsistency")
    public Boolean validateConsistency;

}
