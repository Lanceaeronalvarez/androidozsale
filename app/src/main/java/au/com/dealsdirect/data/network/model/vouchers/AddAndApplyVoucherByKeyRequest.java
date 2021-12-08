package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.network.model.checkout.ApiDeliveryDetails;

public class AddAndApplyVoucherByKeyRequest {

    @SerializedName("languageID")
    @Expose
    private final String languageId;
    @SerializedName("key")
    @Expose
    private final String key;
    @SerializedName("imageSize")
    @Expose
    private final int imageSize;
    @SerializedName("deliveryDetails")
    @Expose
    private final ApiDeliveryDetails deliveryDetails;

    public AddAndApplyVoucherByKeyRequest(String key, String postcode, String pickupPoint, int imageSize, String languageId) {
        this.key = key;
        this.imageSize = imageSize;
        this.languageId = languageId;
        this.deliveryDetails = new ApiDeliveryDetails(postcode, pickupPoint);
    }
}
