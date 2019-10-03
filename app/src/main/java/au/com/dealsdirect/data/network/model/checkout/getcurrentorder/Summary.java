package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.SerializedName;

public class Summary {
    public Double getSubtotal() {
        return subtotal != null ? subtotal : 0;
    }

    public Double getDelivery() {
        return delivery != null ? delivery : 0;
    }

    public Double getDiscount() {
        return discount != null ? discount : 0;
    }

    public Double getTotal() {
        return total != null ? total : 0;
    }

    public Double getTax() {
        return tax != null ? tax : 0;
    }

    public Double getSelect() {
        return select;
    }

    public Double getTotalWithSelect() {
        return totalWithSelect;
    }

    public Double getAmountToFreeDelivery() {
        return amountToFreeDelivery == null ? 0 : amountToFreeDelivery;
    }

    @SerializedName(value = "Subtotal", alternate = {"subtotal"})
    private Double subtotal;
    @SerializedName(value = "Delivery", alternate = {"delivery"})
    private Double delivery;
    @SerializedName(value = "Discount", alternate = {"discount"})
    private Double discount;
    @SerializedName(value = "Total", alternate = {"total"})
    private Double total;
    @SerializedName(value = "Tax", alternate = {"tax"})
    private Double tax;
    @SerializedName(value = "Select", alternate = {"select"})
    private Double select;
    @SerializedName("TotalWithSelect")
    private Double totalWithSelect;
    @SerializedName("AmountToFreeDelivery")
    private Double amountToFreeDelivery;

}
