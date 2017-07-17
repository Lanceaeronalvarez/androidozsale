package au.com.dealsdirect.data.network.model.saleitemdetails;

/**
 * Created by smartwave on 11/07/2017.
 */

public class AddToCartRequest {
    private final String skuId;

    public AddToCartRequest(String id) {
        skuId = id;
    }
}
