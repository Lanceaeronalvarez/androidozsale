package au.com.dealsdirect.data.network.model.productdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;

/**
 * Created by MTC on 2019-12-17.
 */
public class GetYouMayAlsoLikeResponse extends SaleItemProduct {
    @SerializedName("masterProductId")
    @Expose
    private String masterProductId;

    public String getMasterProductId() {
        return masterProductId;
    }
}
