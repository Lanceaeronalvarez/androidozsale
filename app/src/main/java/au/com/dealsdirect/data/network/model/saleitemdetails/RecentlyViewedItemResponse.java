package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;

/**
 * Created by MTC on 2019-12-27.
 */
public class RecentlyViewedItemResponse extends SaleItemProduct {
    @SerializedName("masterProductId")
    @Expose
    private String masterProductId;

    public RecentlyViewedItemResponse(String imageURL, String seoIdentifierId, String productName, String productBrand, String price, boolean isFreeDelivery, boolean isSoldOut) {
        super(imageURL, seoIdentifierId, productName, productBrand, price, isFreeDelivery, isSoldOut);
    }

    public String getMasterProductId() {
        return masterProductId;
    }
}
