package au.com.dealsdirect.data.network.model.checkout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdjustOrderItem {

    public static class RequestValue {
        @SerializedName("languageID")
        @Expose
        private final String languageId;
        @SerializedName("itemID")
        @Expose
        private final String itemId;
        @SerializedName("deliveryDetails")
        @Expose
        private final ApiDeliveryDetails deliveryDetails;
        @SerializedName("imageSize")
        @Expose
        private final String imageSize = "100";

        public RequestValue(String itemId, String postcode, String pickupPoint, String languageId) {
            this.itemId = itemId;
            this.languageId = languageId;
            this.deliveryDetails = new ApiDeliveryDetails(postcode, pickupPoint);
        }
    }

}
