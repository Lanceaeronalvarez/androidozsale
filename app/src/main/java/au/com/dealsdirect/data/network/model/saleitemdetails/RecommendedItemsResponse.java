package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;

/**
 * Created by MTC on 2020-01-02.
 */
public class RecommendedItemsResponse extends SaleItemProduct {
    @SerializedName("masterProductId")
    @Expose
    private String masterProductId;

    public String getMasterProductId() {
        return masterProductId;
    }
}
