
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhoneNumberFormat {

    @SerializedName("Label")
    @Expose
    private String label;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("DataType")
    @Expose
    private String dataType;
    @SerializedName("Class")
    @Expose
    private String _class;
    @SerializedName("Value")
    @Expose
    private Object value;
    @SerializedName("MaxLength")
    @Expose
    private Integer maxLength;
    @SerializedName("MinLength")
    @Expose
    private Object minLength;
    @SerializedName("Validate")
    @Expose
    private String validate;
    @SerializedName("ReadOnly")
    @Expose
    private Boolean readOnly;
    @SerializedName("WithoutComma")
    @Expose
    private Boolean withoutComma;
    @SerializedName("Options")
    @Expose
    private Object options;
    @SerializedName("Regexp")
    @Expose
    private String regexp;
    @SerializedName("ValidateConsistency")
    @Expose
    private Boolean validateConsistency;

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDataType() {
        return dataType;
    }

    public String get_class() {
        return _class;
    }

    public Object getValue() {
        return value;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public Object getMinLength() {
        return minLength;
    }

    public String getValidate() {
        return validate;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public Boolean getWithoutComma() {
        return withoutComma;
    }

    public Object getOptions() {
        return options;
    }

    public String getRegexp() {
        return regexp;
    }

    public Boolean getValidateConsistency() {
        return validateConsistency;
    }
}
