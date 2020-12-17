package au.com.dealsdirect.data.network.model.agerestriction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SaveAgeRestrictedConsentDataRequest {
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("postcode")
    @Expose
    public String postcode;
    @SerializedName("countryID")
    @Expose
    public String countryId;

    public SaveAgeRestrictedConsentDataRequest(String date, String postcode, String countryId) {
        this.date = date;
        this.postcode = postcode;
        this.countryId = countryId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }
}
