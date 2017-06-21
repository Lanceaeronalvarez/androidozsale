package au.com.dealsdirect.data.network.model.address;

import java.util.ArrayList;

/**
 * Created by smartwave on 07/01/2017.
 */

public class DecorationInfoList {

    public String Label;
    public String Name;
    public String Type;
    public String DataType;
    public String Class;
    public String Value;
    public int MaxLength;
    public int MinLength;
    public String Validate;
    public boolean ReadOnly;
    public boolean WithoutComma;
    public ArrayList<String> Options;
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
