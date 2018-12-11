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
    public String Label;
    @Expose
    @SerializedName(value = "Name", alternate = {"name"})
    public String Name;
    @Expose
    @SerializedName(value = "Type", alternate = {"type"})
    public String Type;
    @Expose
    @SerializedName(value = "DataType", alternate = {"dateType"})
    public String DataType;
    @Expose
    @SerializedName(value = "Class", alternate = {"class"})
    public String Class;
    @Expose
    @SerializedName(value = "Value", alternate = {"value"})
    public String Value;
    @Expose
    @SerializedName(value = "MaxLength", alternate = {"maxLength"})
    public int MaxLength;
    @Expose
    @SerializedName(value = "MinLength", alternate = {"minLength"})
    public int MinLength;
    @Expose
    @SerializedName(value = "Validate", alternate = {"validate"})
    public String Validate;
    @Expose
    @SerializedName(value = "ReadOnly", alternate = {"readOnly"})
    public boolean ReadOnly;
    @Expose
    @SerializedName(value = "WithoutComma", alternate = {"withoutComma"})
    public boolean WithoutComma;
    @Expose
    @SerializedName(value = "Options", alternate = {"options"})
    public ArrayList<String> Options;
    @Expose
    @SerializedName(value = "Regexp", alternate = {"regexp"})
    public String Regexp;

    public boolean isValidateConsistency() {
        return ValidateConsistency;
    }

    public void setValidateConsistency(boolean validateConsistency) {
        ValidateConsistency = validateConsistency;
    }

    public String getRegexp() {
        return Regexp;
    }

    public void setRegexp(String regexp) {
        Regexp = regexp;
    }

    public ArrayList<String> getOptions() {
        return Options;
    }

    public void setOptions(ArrayList<String> options) {
        Options = options;
    }

    public boolean isWithoutComma() {
        return WithoutComma;
    }

    public void setWithoutComma(boolean withoutComma) {
        WithoutComma = withoutComma;
    }

    public boolean isReadOnly() {
        return ReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        ReadOnly = readOnly;
    }

    public String getValidate() {
        return Validate;
    }

    public void setValidate(String validate) {
        Validate = validate;
    }

    public int getMinLength() {
        return MinLength;
    }

    public void setMinLength(int minLength) {
        MinLength = minLength;
    }

    public int getMaxLength() {
        return MaxLength;
    }

    public void setMaxLength(int maxLength) {
        MaxLength = maxLength;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getClassType() {
        return this.Class;
    }

    public void setClass(String aClass) {
        Class = aClass;
    }

    public String getDataType() {
        return DataType;
    }

    public void setDataType(String dataType) {
        DataType = dataType;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public boolean ValidateConsistency;
}
