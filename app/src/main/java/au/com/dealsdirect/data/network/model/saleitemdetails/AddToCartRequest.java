package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.newrelic.com.google.gson.annotations.SerializedName;

/**
 * Created by smartwave on 11/07/2017.
 */

public class AddToCartRequest {
    @Expose
    @SerializedName("skudId")
    private String skuId;

    private String itemName;

    private double price;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
