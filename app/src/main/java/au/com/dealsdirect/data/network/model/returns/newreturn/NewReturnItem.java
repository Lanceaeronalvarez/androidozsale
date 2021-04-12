
package au.com.dealsdirect.data.network.model.returns.newreturn;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class NewReturnItem {
    @SerializedName("quantity")
    @Expose
    int quantity;
    @SerializedName("price")
    @Expose
    double price;
    @SerializedName("price_total")
    @Expose
    double priceTotal;
    @SerializedName("type")
    @Expose
    String type;
    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("image_url")
    @Expose
    String imageUrl;
    @SerializedName("size")
    @Expose
    String size;

    public NewReturnItem() {
    }

    public NewReturnItem(NewReturnItem item) {
        id = item.getId();
        imageUrl = item.getImageUrl();
        name = item.getName();
        price = item.getPrice();
        priceTotal = item.getPriceTotal();
        quantity = item.getQuantity();
        size = item.getSize();
        type = item.getType();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(double priceTotal) {
        this.priceTotal = priceTotal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof NewReturnItem) {
            final NewReturnItem other = (NewReturnItem) obj;
            return other.id.equals(id) &&
                    other.name.equals(name) &&
                    other.size.equals(size) &&
                    other.price == price;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, size, price);
    }
}
