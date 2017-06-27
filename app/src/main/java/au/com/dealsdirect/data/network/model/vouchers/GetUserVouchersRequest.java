package au.com.dealsdirect.data.network.model.vouchers;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Paul on 6/23/17.
 */

public class GetUserVouchersRequest {

    @SerializedName("languageID")
    @Expose
    String languageId;

    public GetUserVouchersRequest(String languageId) {
        this.languageId = languageId;
    }
}
