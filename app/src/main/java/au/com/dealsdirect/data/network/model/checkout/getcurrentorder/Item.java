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

    @SerializedName(value = "ID", alternate = {"id"})
    public String id;
    @SerializedName(value = "ItemID", alternate = {"itemID"})
    public String itemID;
    @SerializedName(value = "Item", alternate = {"item"})
    public String item;
    @SerializedName(value = "Size", alternate = {"size"})
    public String size;
    @SerializedName(value = "Qty", alternate = {"qty"})
    public Integer qty;
    @SerializedName(value = "Price", alternate = {"price"})
    public double price;
    @SerializedName(value = "Subtotal", alternate = {"subtotal"})
    public double subtotal;
    @SerializedName(value = "BrandID", alternate = {"brandID"})
    public String brandID;
    @SerializedName(value = "ImageID", alternate = {"imageID"})
    public String imageID;
    @SerializedName(value = "FileName", alternate = {"fileName"})
    public String fileName;
    @SerializedName(value = "SaleID", alternate = {"saleID"})
    public String saleID;
    @SerializedName(value = "CustomizableItemDetails", alternate = {"customizableItemDetails"})
    private List<Personalisation.CustomizableItemDetails> customizableItemDetailsList;
}
