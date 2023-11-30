
package au.com.dealsdirect.data.network.model.returns.returnorders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetReturnOrders {
    @SerializedName("invoice_number")
    @Expose
    int invoiceNumber;
    @SerializedName("status")
    @Expose
    String status;
    @SerializedName("tracking")
    @Expose
    String tracking;
    @SerializedName("is_requested")
    @Expose
    boolean isRequested;
    @SerializedName("items")
    @Expose
    List<Item> items;
    @SerializedName("items_count")
    @Expose
    int itemsCount;
    @SerializedName("total_amount")
    @Expose
    double totalAmount;

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public boolean isRequested() {
        return isRequested;
    }

    public void setRequested(boolean requested) {
        isRequested = requested;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public static class Item {
        @SerializedName("type")
        @Expose
        String type;
        @SerializedName("id")
        @Expose
        String id;
        @SerializedName("image_url")
        @Expose
        String imageUrl;
        @SerializedName("quantity")
        @Expose
        int quantity;
        @SerializedName("price")
        @Expose
        double price;
        @SerializedName("price_total")
        @Expose
        double priceTotal;

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

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
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
    }
}
