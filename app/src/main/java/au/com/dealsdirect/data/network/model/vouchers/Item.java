
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("ItemID")
    @Expose
    private String itemID;
    @SerializedName("Item")
    @Expose
    private String item;
    @SerializedName("Size")
    @Expose
    private String size;
    @SerializedName("Qty")
    @Expose
    private Integer qty;
    @SerializedName("Price")
    @Expose
    private Double price;
    @SerializedName("Subtotal")
    @Expose
    private Double subtotal;
    @SerializedName("BrandID")
    @Expose
    private String brandID;
    @SerializedName("ImageID")
    @Expose
    private String imageID;
    @SerializedName("FileName")
    @Expose
    private String fileName;

    public String getiD() {
        return iD;
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

    public Double getPrice() {
        return price;
    }

    public Double getSubtotal() {
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
}
