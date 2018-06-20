package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by smartwave on 11/07/2017.
 */

public class AddToCartRequest {

    @SerializedName("personalizationData")
    @Expose
    private HashMap<String, String> personalizationData;
    @SerializedName("skuId")
    @Expose
    private String skuId;

    private transient String itemName;

    private transient double price;

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

    public HashMap<String, String> getPersonalizationData() {
        return personalizationData;
    }

    public void setPersonalizationData(HashMap<String, String> personalizationData) {
        this.personalizationData = personalizationData;
    }
}
