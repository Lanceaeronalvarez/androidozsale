package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MTC on 2019-12-27.
 */
public class RecentlyViewedItemRequest {
    @SerializedName("productId")
    @Expose
    private String productId;

    @SerializedName("masterSkuId")
    @Expose
    private String masterSkuId;

    public RecentlyViewedItemRequest(String productId, String masterSkuId) {
        this.productId = productId;
        this.masterSkuId = masterSkuId;
    }
}
