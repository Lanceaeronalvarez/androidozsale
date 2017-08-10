package au.com.dealsdirect.data.network.model.ourpayverificationnormalizephone;

import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 8/9/17.
 */

public class VerificationNormalizePhoneResponseValue {

    @SerializedName("CountryCode")
    private String countryCode;

    @SerializedName("Phone")
    private String phone;

    @SerializedName("ErrorMessage")
    private String errorMessage;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
