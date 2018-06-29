package au.com.dealsdirect.data.network.model.consentdata;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentDataRequest {

    @SerializedName("countryID")
    @Expose
    private String countryID;

    public ConsentDataRequest(String countryID) {
        this.countryID = countryID;
    }
}