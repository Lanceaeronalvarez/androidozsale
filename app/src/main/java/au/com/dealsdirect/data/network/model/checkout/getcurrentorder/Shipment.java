package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Shipment {
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("DeliveryPrice")
    @Expose
    private Double deliveryPrice;
    @SerializedName("MinimumSpendForPromoPrice")
    @Expose
    private Double minimumSpendForPromoPrice;
    @SerializedName("AmountToPromoPrice")
    @Expose
    private Double amountToPromoPrice;
    @SerializedName("Items")
    @Expose
    private List<String> items;
    @SerializedName("LocationFilterHash")
    @Expose
    private String locationFilterHash;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDeliveryPrice() {
        return deliveryPrice == null ? 0 : deliveryPrice;
    }

    public void setDeliveryPrice(double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public double getMinimumSpendForPromoPrice() {
        return minimumSpendForPromoPrice == null ? 0 : minimumSpendForPromoPrice;
    }

    public void setMinimumSpendForPromoPrice(double minimumSpendForPromoPrice) {
        this.minimumSpendForPromoPrice = minimumSpendForPromoPrice;
    }

    public double getAmountToPromoPrice() {
        return amountToPromoPrice == null ? 0 : amountToPromoPrice;
    }

    public void setAmountToPromoPrice(double amountToPromoPrice) {
        this.amountToPromoPrice = amountToPromoPrice;
    }

    public String getLocationFilterHash() {
        return locationFilterHash;
    }
}
