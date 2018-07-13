package au.com.dealsdirect.data.network.model.gdpr.savereceivesales;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by smartwave on 12/07/2018.
 */

public class SaveReceiveSalesRequest {

    @SerializedName("countryID")
    @Expose
    private String countryID;
    @SerializedName("languageID")
    @Expose
    private String languageId;
    @SerializedName("receiveInvitations")
    @Expose
    private boolean receiveInvitations;

    public SaveReceiveSalesRequest(String countryID, String languageId, boolean receiveInvitations) {
        this.countryID = countryID;
        this.languageId = languageId;
        this.receiveInvitations = receiveInvitations;
    }
}
