package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;

/**
 * Created by MTC on 2020-01-02.
 */
public class RecommendedItemsResponse extends SaleItemProduct {
    @SerializedName("masterProductId")
    @Expose
    private String masterProductId;

    public RecommendedItemsResponse(String imageURL, String seoIdentifierId, String productName, String productBrand, String price, boolean isFreeDelivery, boolean isSoldOut) {
        super(imageURL, seoIdentifierId, productName, productBrand, price, isFreeDelivery, isSoldOut);
    }

    public String getMasterProductId() {
        return masterProductId;
    }
}
