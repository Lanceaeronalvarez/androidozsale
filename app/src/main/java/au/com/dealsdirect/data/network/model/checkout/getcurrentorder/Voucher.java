package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.SerializedName;

public class Voucher {
    @SerializedName("ID")
    public String id;

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    @SerializedName("Description")
    public String description;
}
