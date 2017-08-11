package au.com.dealsdirect.data.network.model.legalities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dp on 7/14/17.
 */

public class GetTemplateTextsRequest {

    @SerializedName("countryID")
    @Expose
    public String countryId;

    @SerializedName("languageID")
    @Expose
    public String languageId;

    @SerializedName("templateKeys")
    @Expose
    public String[] templateKeys;
}
