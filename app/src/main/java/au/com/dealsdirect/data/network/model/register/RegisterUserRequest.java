package au.com.dealsdirect.data.network.model.register;

import java.util.HashMap;

/**
 * dp Created by Admin on 6/27/17.
 */

public class RegisterUserRequest {

    String languageID;
    String countryID;
    int clientType;
    String foreName;
    String surName;
    String email;
    String password;
    String referredBy;
    String invitedBy;
    String voucherID;
    boolean tcWasRead;
    HashMap<String, Boolean> parameters;

    String captchaResponse;

    // 2 for apps https://apacsale.atlassian.net/wiki/spaces/CX/pages/708641008/V3.26
    int clientID = 1;

    public RegisterUserRequest(
            String languageID, String countryID, int clientType,
            String foreName, String surName, String email, String password,
            String referredBy, String invitedBy, String voucherID,
            boolean tcAccepted, boolean emailsAccepted, String captchaResponse) {

        this.languageID = languageID;
        this.countryID = countryID;
        this.clientType = clientType;
        this.foreName = foreName.trim();
        this.surName = surName.trim();
        this.email = email;
        this.password = password;
        this.referredBy = referredBy;
        this.invitedBy = invitedBy;
        this.voucherID = voucherID;
        this.parameters = new HashMap<>();
        this.parameters.put("tcAccepted", tcAccepted);
        this.parameters.put("emailsAccepted", emailsAccepted);
        this.captchaResponse = captchaResponse;
    }

    public void setToGdprDisabled(){
        this.parameters = null;
        this.tcWasRead = true;
    }

    public String getLanguageID() {
        return languageID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public String getForeName() {
        return foreName;
    }

    public void setForeName(String foreName) {
        this.foreName = foreName.trim();
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }

    public String getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
    }

    public String getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(String voucherID) {
        this.voucherID = voucherID;
    }

    public String getCaptchaResponse() {
        return captchaResponse;
    }

    public void setCaptchaResponse(String captchaResponse) {
        this.captchaResponse = captchaResponse;
    }
}
