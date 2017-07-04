package au.com.dealsdirect.data.network.model.invite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Paul on 7/2/17.
 */

public class SetInviteRequest {

    public String inviteLink;

    @SerializedName("languageID")
    @Expose
    public String languageId;
}
