package au.com.dealsdirect.data.network.model.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by smartwave on 07/01/2017.
 */

public class DecorationInfoList {

    @Expose
    @SerializedName(value = "Label", alternate = {"label"})
    private String label;
    @Expose
    @SerializedName(value = "Name", alternate = {"name"})
    private String name;
    @Expose
    @SerializedName(value = "Type", alternate = {"type"})
    private String type;
    @Expose
    @SerializedName(value = "DataType", alternate = {"dateType"})
    private String dataType;
    @Expose
    @SerializedName(value = "Class", alternate = {"class"})
    private String decorationClass;
    @Expose
    @SerializedName(value = "Value", alternate = {"value"})
    private String value;
    @Expose
    @SerializedName(value = "MaxLength", alternate = {"maxLength", "max_length"})
    private int maxLength;
    @Expose
    @SerializedName(value = "MinLength", alternate = {"minLength", "min_length"})
    private int minLength;
    @Expose
    @SerializedName(value = "Validate", alternate = {"validate"})
    private String validate;
    @Expose
    @SerializedName(value = "ReadOnly", alternate = {"readOnly", "read_only"})
    private boolean readOnly;
    @Expose
    @SerializedName(value = "WithoutComma", alternate = {"withoutComma", "without_comma"})
    private boolean withoutComma;
    @Expose
    @SerializedName(value = "Options", alternate = {"options"})
    private ArrayList<String> options;
    @Expose
    @SerializedName(value = "Regexp", alternate = {"regexp"})
    private String regexp;
    @Expose
    @SerializedName("validate_consistency")
    private boolean validateConsistency;

    public boolean isValidateConsistency() {
        return ValidateConsistency;
    }

    public void setValidateConsistency(boolean validateConsistency) {
        ValidateConsistency = validateConsistency;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public boolean isWithoutComma() {
        return withoutComma;
    }

    public void setWithoutComma(boolean withoutComma) {
        this.withoutComma = withoutComma;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getValidate() {
        return validate;
    }

    public void setValidate(String validate) {
        this.validate = validate;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getClassType() {
        return decorationClass;
    }

    public void setClass(String aClass) {
        decorationClass = aClass;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean ValidateConsistency;
}
