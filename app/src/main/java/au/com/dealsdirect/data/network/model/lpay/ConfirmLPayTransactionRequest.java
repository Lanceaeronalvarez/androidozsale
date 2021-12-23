package au.com.dealsdirect.data.network.model.lpay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConfirmLPayTransactionRequest {
    @SerializedName("countryID")
    @Expose
    private String countryId = null;
    @SerializedName("languageID")
    @Expose
    private String languageId = null;
    @SerializedName("data")
    @Expose
    private final Data data;

    public ConfirmLPayTransactionRequest() {
        this.data = null;
    }

    public ConfirmLPayTransactionRequest(String token, String signature, String reference) {
        this.data = new Data(token, signature, reference);
    }

    public ConfirmLPayTransactionRequest(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public static class Data {
        @SerializedName("token")
        @Expose
        private final String token;

        @SerializedName("signature")
        @Expose
        private final String signature;

        @SerializedName("reference")
        @Expose
        private final String reference;

        public Data(String token, String signature, String reference) {
            this.token = token;
            this.signature = signature;
            this.reference = reference;
        }

        public String getToken() {
            return token;
        }

        public String getSignature() {
            return signature;
        }

        public String getReference() {
            return reference;
        }
    }
}
