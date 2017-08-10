package au.com.dealsdirect.data.network.model.ourpayverificationcodeconfirm;

/**
 * dp Created by Admin on 8/10/17.
 */

public class VerificationCodeConfirmRequest {

    public String phone;

    public String countryID;

    public String code;

    public String languageID;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguageID() {
        return languageID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }
}
