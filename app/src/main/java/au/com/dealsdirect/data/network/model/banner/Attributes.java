
package au.com.dealsdirect.data.network.model.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attributes {

    @SerializedName("saleExternalId")
    @Expose
    private String saleExternalId;

    public String getSaleExternalId() {
        return saleExternalId;
    }

    public void setSaleExternalId(String saleExternalId) {
        this.saleExternalId = saleExternalId;
    }

}
