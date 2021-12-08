package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.network.model.checkout.ApiDeliveryDetails;

public class AddVoucherByKeyRequest {
    @SerializedName("key")
    @Expose
    private final String key;
    @SerializedName("deliveryDetails")
    @Expose
    private final ApiDeliveryDetails deliveryDetails;
    @SerializedName("imageSize")
    @Expose
    private final int imageSize;
    @SerializedName("languageID")
    @Expose
    private final String languageId;

    public AddVoucherByKeyRequest(String key, String postcode, String pickupPoint, int imageSize, String languageId) {
        this.key = key;
        this.deliveryDetails = new ApiDeliveryDetails(postcode, pickupPoint);
        this.imageSize = imageSize;
        this.languageId = languageId;
    }
}
