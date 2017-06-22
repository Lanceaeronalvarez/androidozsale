package au.com.dealsdirect.data.network.model.language;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Paul on 6/22/17.
 */

public class GetUserLanguageRequest {
    @SerializedName("countryID")
    @Expose
    String countryId;

    public GetUserLanguageRequest(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }
}
