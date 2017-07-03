
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Voucher {

    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("Description")
    @Expose
    private String description;

    public String getID() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
