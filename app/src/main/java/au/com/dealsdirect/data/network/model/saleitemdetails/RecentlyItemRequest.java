package au.com.dealsdirect.data.network.model.saleitemdetails;

/**
 * Created by MTC on 2019-12-27.
 */
public class RecentlyItemRequest {
    private String productId;
    private String masterSkuId;

    public RecentlyItemRequest(String productId, String masterSkuId) {
        this.productId = productId;
        this.masterSkuId = masterSkuId;
    }
}
