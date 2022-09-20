package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.network.model.checkout.ApiDeliveryDetails;

public class RemoveVoucherByKeyRequest {
    @SerializedName("key")
    @Expose
    private final String key;
    @SerializedName("imageSize")
    @Expose
    private final int imageSize;
    @SerializedName("deliveryDetails")
    @Expose
    private final ApiDeliveryDetails deliveryDetails;

    public RemoveVoucherByKeyRequest(String key, int imageSize, ApiDeliveryDetails deliveryDetails) {
        this.key = key;
        this.imageSize = imageSize;
        this.deliveryDetails = deliveryDetails;
    }
}
