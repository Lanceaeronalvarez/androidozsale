package au.com.dealsdirect.data.network.model.language;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Paul on 6/22/17.
 */

public class SetUserLanguageRequest {
    @SerializedName("countryID")
    @Expose
    String countryId;

    @SerializedName("languageID")
    @Expose
    String languageId;

    public SetUserLanguageRequest(String countryId, String languageId) {
        this.countryId = countryId;
        this.languageId = languageId;
    }
}
