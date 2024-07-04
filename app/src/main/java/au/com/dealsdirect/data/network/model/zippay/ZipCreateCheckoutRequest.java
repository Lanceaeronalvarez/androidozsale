package au.com.dealsdirect.data.network.model.zippay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZipCreateCheckoutRequest {
    @SerializedName("countryID")
    @Expose
    private String countryId;

    @SerializedName("languageID")
    @Expose
    private String languageId;

    @SerializedName("data")
    @Expose
    private Data data;

    public ZipCreateCheckoutRequest(String countryId, String languageId, String redirectUri) {
        this.countryId = countryId;
        this.languageId = languageId;
        this.data = new Data(redirectUri);
    }

    public static class Data {
        @SerializedName("redirecturi")
        @Expose
        private String redirectUri;

        public Data(String redirectUri) {
            this.redirectUri = redirectUri;
        }
    }
}
