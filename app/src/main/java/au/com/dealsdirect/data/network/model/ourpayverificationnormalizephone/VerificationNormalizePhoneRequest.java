package au.com.dealsdirect.data.network.model.ourpayverificationnormalizephone;

/**
 * dp Created by Admin on 8/9/17.
 */

public class VerificationNormalizePhoneRequest {

    public String countryID;
    public String languageID;
    public String phone;
    public String code;

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getLanguageID() {
        return languageID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
