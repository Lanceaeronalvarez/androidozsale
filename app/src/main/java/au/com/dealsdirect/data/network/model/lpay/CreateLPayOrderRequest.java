package au.com.dealsdirect.data.network.model.lpay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateLPayOrderRequest {
    @SerializedName("countryID")
    @Expose
    private String countryId = null;
    @SerializedName("languageID")
    @Expose
    private String languageId = null;
    @SerializedName("data")
    @Expose
    private final Data data;

    public CreateLPayOrderRequest() {
        this.data = null;
    }

    public CreateLPayOrderRequest(String redirectUri) {
        this.data = new Data(redirectUri);
    }

    public CreateLPayOrderRequest(Data data) {
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

        @SerializedName("redirecturi")
        @Expose
        private final String redirectUri;

        public Data(String redirectUri) {
            this.redirectUri = redirectUri;
        }

        public String getRedirectUri() {
            return redirectUri;
        }
    }
}
