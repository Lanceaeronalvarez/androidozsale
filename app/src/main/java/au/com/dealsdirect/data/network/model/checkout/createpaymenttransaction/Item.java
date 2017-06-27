
package au.com.dealsdirect.data.network.model.checkout.createpaymenttransaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("ProductName")
    @Expose
    private String productName;
    @SerializedName("Sku")
    @Expose
    private String sku;
    @SerializedName("SizeName")
    @Expose
    private String sizeName;
    @SerializedName("Price")
    @Expose
    private double price;
    @SerializedName("Quantity")
    @Expose
    private Integer quantity;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
