
package au.com.dealsdirect.data.network.model.returns.returndetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("BrandID")
    @Expose
    private String brandID;
    @SerializedName("ItemID")
    @Expose
    private String itemID;
    @SerializedName("ImageID")
    @Expose
    private String imageID;
    @SerializedName("File")
    @Expose
    private String file;
    @SerializedName("Item")
    @Expose
    private String item;
    @SerializedName("Size")
    @Expose
    private String size;
    @SerializedName("Count")
    @Expose
    private Integer count;
    @SerializedName("Price")
    @Expose
    private Double price;
    @SerializedName("SubTotal")
    @Expose
    private Double subTotal;

    public String getBrandID() {
        return brandID;
    }

    public void setBrandID(String brandID) {
        this.brandID = brandID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

}
