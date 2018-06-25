package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.SerializedName;

public class Summary {
    public Double getSubtotal() {
        return subtotal;
    }

    public Double getDelivery() {
        return delivery;
    }

    public Double getDiscount() {
        return discount;
    }

    public Double getTotal() {
        return total;
    }

    public Double getTax() {
        return tax;
    }

    @SerializedName("Subtotal")
    private Double subtotal;
    @SerializedName("Delivery")
    private Double delivery;
    @SerializedName("Discount")
    private Double discount;
    @SerializedName("Total")
    private Double total;
    @SerializedName("Tax")
    private Double tax;
}
