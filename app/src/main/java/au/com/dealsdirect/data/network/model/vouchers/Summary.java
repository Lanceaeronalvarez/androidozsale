
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Summary {

    @SerializedName("Subtotal")
    @Expose
    private Double subtotal;
    @SerializedName("Delivery")
    @Expose
    private Double delivery;
    @SerializedName("Discount")
    @Expose
    private Double discount;
    @SerializedName("Total")
    @Expose
    private Double total;
    @SerializedName("Tax")
    @Expose
    private Integer tax;

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

    public Integer getTax() {
        return tax;
    }
}
