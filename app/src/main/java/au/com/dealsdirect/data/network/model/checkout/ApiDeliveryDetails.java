package au.com.dealsdirect.data.network.model.checkout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiDeliveryDetails {
    @SerializedName("Postcode")
    @Expose
    private final String postcode;
    @SerializedName("PickupPoint")
    @Expose
    private final String pickupPoint;

    public ApiDeliveryDetails(String postcode, String pickupPoint) {
        this.postcode = postcode;
        this.pickupPoint = pickupPoint;
    }
}
