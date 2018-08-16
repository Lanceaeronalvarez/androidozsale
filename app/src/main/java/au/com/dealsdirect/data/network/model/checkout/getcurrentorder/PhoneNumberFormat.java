package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.SerializedName;

public class PhoneNumberFormat {
    @SerializedName(value = "Label", alternate = {"label"})
    public String label;
    @SerializedName(value = "Name", alternate = {"name"})
    public String name;
    @SerializedName(value = "Type", alternate = {"type"})
    public String type;
    @SerializedName(value = "DataType", alternate = {"dataType"})
    public String dataType;
    @SerializedName(value = "Class", alternate = {"class"})
    public String _class;
    @SerializedName(value = "Value", alternate = {"value"})
    public Object value;
    @SerializedName(value = "MaxLength", alternate = {"maxLength"})
    public Integer maxLength;
    @SerializedName(value = "MinLength", alternate = {"minLength"})
    public Object minLength;
    @SerializedName(value = "Validate", alternate = {"validate"})
    public String validate;
    @SerializedName(value = "ReadOnly", alternate = {"readOnly"})
    public Boolean readOnly;
    @SerializedName(value = "WithoutComma", alternate = {"withoutComma"})
    public Boolean withoutComma;
    @SerializedName(value = "Options", alternate = {"options"})
    public Object options;
    @SerializedName(value = "Regexp", alternate = {"regexp"})
    public String regexp;
    @SerializedName(value = "ValidateConsistency", alternate = {"validateConsistency"})
    public Boolean validateConsistency;
}
