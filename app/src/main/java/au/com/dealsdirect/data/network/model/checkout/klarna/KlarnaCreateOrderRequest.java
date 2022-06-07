package au.com.dealsdirect.data.network.model.checkout.klarna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KlarnaCreateOrderRequest {
    @SerializedName("countryID")
    @Expose
    private String countryId;

    @SerializedName("languageID")
    @Expose
    private String languageId;

    @SerializedName("authorizationToken")
    @Expose
    private String authorizationToken;

    public KlarnaCreateOrderRequest(String countryId, String languageId, String authorizationToken) {
        this.countryId = countryId;
        this.languageId = languageId;
        this.authorizationToken = authorizationToken;
    }
}
