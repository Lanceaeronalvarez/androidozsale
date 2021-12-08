package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.com.dealsdirect.data.network.model.checkout.ApiDeliveryDetails;

public class ApplyVouchersRequest {
    @SerializedName("vouchers")
    @Expose
    private final List<String> vouchers;
    @SerializedName("imageSize")
    @Expose
    private final int imageSize;
    @SerializedName("languageID")
    @Expose
    private final String languageId;
    @SerializedName("deliveryDetails")
    @Expose
    private final ApiDeliveryDetails deliveryDetails;

    public ApplyVouchersRequest(List<String> vouchers, String postcode, String pickupPoint, int imageSize, String languageId) {
        this.vouchers = vouchers;
        this.imageSize = imageSize;
        this.languageId = languageId;
        this.deliveryDetails = new ApiDeliveryDetails(postcode, pickupPoint);
    }
}
