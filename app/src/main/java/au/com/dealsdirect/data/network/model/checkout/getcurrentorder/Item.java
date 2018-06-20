package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.com.dealsdirect.data.network.model.saleitemdetails.Personalisation;

public class Item {

    public String getId() {
        return id;
    }

    public String getItemID() {
        return itemID;
    }

    public String getItem() {
        return item;
    }

    public String getSize() {
        return size;
    }

    public Integer getQty() {
        return qty;
    }

    public double getPrice() {
        return price;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public String getBrandID() {
        return brandID;
    }

    public String getImageID() {
        return imageID;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSaleID() {
        return saleID;
    }

    public List<Personalisation.CustomizableItemDetails> getCustomizableItemDetailsList() {
        return customizableItemDetailsList;
    }

    @SerializedName("ID")
    public String id;
    @SerializedName("ItemID")
    public String itemID;
    @SerializedName("Item")
    public String item;
    @SerializedName("Size")
    public String size;
    @SerializedName("Qty")
    public Integer qty;
    @SerializedName("Price")
    public double price;
    @SerializedName("Subtotal")
    public double subtotal;
    @SerializedName("BrandID")
    public String brandID;
    @SerializedName("ImageID")
    public String imageID;
    @SerializedName("FileName")
    public String fileName;
    @SerializedName("SaleID")
    public String saleID;
    @SerializedName("CustomizableItemDetails")
    private List<Personalisation.CustomizableItemDetails> customizableItemDetailsList;
}
