package au.com.dealsdirect.data.network.model.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MTC on 2019-07-25.
 */
public class OrderReceivedRequest {

    @SerializedName("orderID")
    @Expose
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}
