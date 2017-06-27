package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.SerializedName;

public class Summary {
    @SerializedName("Subtotal")
    public Double subtotal;
    @SerializedName("Delivery")
    public Double delivery;
    @SerializedName("Discount")
    public Double discount;
    @SerializedName("Total")
    public Double total;
    @SerializedName("Tax")
    public Double tax;
}
