package au.com.dealsdirect.data.network.model.afterpay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateAfterpayOrderRequest {
    @SerializedName("countryID")
    @Expose
    private String countryId;

    @SerializedName("languageID")
    @Expose
    private String languageId;

    @SerializedName("redirectUrl")
    @Expose
    private String redirectUrl;

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

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
