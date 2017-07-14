package au.com.dealsdirect.data.network.model.legalities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Paul on 7/14/17.
 */

public class GetTemplateTextRequest {

    @SerializedName("countryID")
    @Expose
    public String countryId;

    @SerializedName("languageID")
    @Expose
    public String languageId;

    public String templateKey;
}
