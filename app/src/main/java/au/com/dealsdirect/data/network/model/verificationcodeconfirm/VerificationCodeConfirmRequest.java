package au.com.dealsdirect.data.network.model.verificationcodeconfirm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 8/10/17.
 */

public class VerificationCodeConfirmRequest {

    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("countryID")
    @Expose
    private String countryID;
    @SerializedName("languageID")
    @Expose
    private String languageID;
    @SerializedName("code")
    @Expose
    private String code;

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
