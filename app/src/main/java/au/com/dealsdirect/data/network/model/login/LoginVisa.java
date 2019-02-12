package au.com.dealsdirect.data.network.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.HashMap;

/**
 * Created by smartwave on 07/02/2018.
 */

public class LoginVisa {


    public static class RequestValue {

        @SerializedName("countryID")
        @Expose
        private String countryID;
        @SerializedName("languageID")
        @Expose
        private String languageID;
        @SerializedName("referredBy")
        @Expose
        private String referredBy;
        @SerializedName("invitedBy")
        @Expose
        private String invitedBy;
        @SerializedName("voucherID")
        @Expose
        private String voucherID;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("parameters")
        @Expose
        private HashMap<String, Boolean> parameters;

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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public void setParameters(boolean tcAccepted, boolean emailsAccepted) {
            parameters = new HashMap<>();
            this.parameters.put("tcAccepted", tcAccepted);
            this.parameters.put("emailsAccepted", emailsAccepted);
        }

        public void setToGdprDisabled(){
            this.parameters = null;
        }

        public static class Data {

            @SerializedName("callID")
            @Expose
            private String callID;
            @SerializedName("encKey")
            @Expose
            private String encKey;
            @SerializedName("encPaymentData")
            @Expose
            private String encPaymentData;
            @SerializedName("loginVisaType")
            @Expose
            private Integer loginVisaType;
            @SerializedName("firstName")
            @Expose
            private String firstName;
            @SerializedName("lastName")
            @Expose
            private String lastName;
            @SerializedName("email")
            @Expose
            private String email;
            @SerializedName("paymentNonce")
            @Expose
            private String paymentNonce;

            public String getCallID() {
                return callID;
            }

            public void setCallID(String callID) {
                this.callID = callID;
            }

            public String getEncKey() {
                return encKey;
            }

            public void setEncKey(String encKey) {
                this.encKey = encKey;
            }

            public String getEncPaymentData() {
                return encPaymentData;
            }

            public void setEncPaymentData(String encPaymentData) {
                this.encPaymentData = encPaymentData;
            }

            public Integer getLoginVisaType() {
                return loginVisaType;
            }

            public void setLoginVisaType(Integer loginVisaType) {
                this.loginVisaType = loginVisaType;
            }

            public String getFirstName() {
                return firstName;
            }

            public void setFirstName(String firstName) {
                this.firstName = firstName;
            }

            public String getLastName() {
                return lastName;
            }

            public void setLastName(String lastName) {
                this.lastName = lastName;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getPaymentNonce() {
                return paymentNonce;
            }

            public void setPaymentNonce(String paymentNonce) {
                this.paymentNonce = paymentNonce;
            }
        }

    }

    public static class ResponseValue{
        private Response d;

        public static class Response extends LegacyBaseResponseValue {
            public Value Value;
        }

        public static class Value {
            public String Ticket;
            public boolean PasswordRequired;
            public String Email;
            public boolean AccountExists;
            public boolean Registered;
        }

        public boolean isAuthenticated(){
            return d.isAuthenticated();
        }

        public boolean getResult(){
            return d.getResult();
        }

        public String getTicket() {
            return d.Value.Ticket;
        }

        public boolean isPasswordRequired(){
            return d.Value.PasswordRequired;
        }

        public boolean isAccountExists(){
            return d.Value.AccountExists;
        }

        public boolean isRegistered(){
            return d.Value.Registered;
        }

        public String getEmail(){
            return d.Value.Email;
        }

        public String getMessage() {
            return d.getMessage();
        }
    }
}
