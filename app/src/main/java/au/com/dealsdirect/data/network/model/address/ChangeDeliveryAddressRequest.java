package au.com.dealsdirect.data.network.model.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MTC on 2019-06-26.
 */
public class ChangeDeliveryAddressRequest {
    @SerializedName("orderID")
    @Expose
    private String orderID;
    @SerializedName("deliveryAddressID")
    @Expose
    private String addressID;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getAddressID() {
        return addressID;
    }

    public void setAddressID(String addressID) {
        this.addressID = addressID;
    }
}
