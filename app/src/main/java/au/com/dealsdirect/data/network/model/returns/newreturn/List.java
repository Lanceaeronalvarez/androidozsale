
package au.com.dealsdirect.data.network.model.returns.newreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class List {

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("Item")
    @Expose
    private String item;
    @SerializedName("Size")
    @Expose
    private Object size;
    @SerializedName("Count")
    @Expose
    private Integer count;
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

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Object getSize() {
        return size;
    }

    public void setSize(Object size) {
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

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public String getBrandID() {
        return brandID;
    }

    public void setBrandID(String brandID) {
        this.brandID = brandID;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
