package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.network.model.checkout.ApiDeliveryDetails;

public class ClearVouchersRequest {
    @SerializedName("deliveryDetails")
    @Expose
    private final ApiDeliveryDetails deliveryDetails;
    @SerializedName("imageSize")
    @Expose
    private final int imageSize;
    @SerializedName("languageID")
    @Expose
    private final String languageId;

    public ClearVouchersRequest(String postcode, String pickupPoint, int imageSize, String languageId) {
        this.deliveryDetails = new ApiDeliveryDetails(postcode, pickupPoint);
        this.imageSize = imageSize;
        this.languageId = languageId;
    }
}
