package au.com.dealsdirect.data.network.model.checkout.klarna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KlarnaCreateSessionRequest {
    @SerializedName("countryID")
    @Expose
    private String countryId;

    @SerializedName("languageID")
    @Expose
    private String languageId;

    public KlarnaCreateSessionRequest(String countryId, String languageId) {
        this.countryId = countryId;
        this.languageId = languageId;
    }
}
