package au.com.dealsdirect.data.network.model.checkout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.utils.AppConstants;

public class GetCurrentOrder {

    public static class RequestValue {
        @SerializedName("deliveryDetails")
        @Expose
        private final ApiDeliveryDetails deliveryDetails;
        @SerializedName("languageID")
        @Expose
        private final String languageId;
        @SerializedName("imageSize")
        @Expose
        private final int imageSize = AppConstants.IMAGE_SIZE;

        public RequestValue(String postcode, String pickupPoint, String languageId) {
            this.deliveryDetails = new ApiDeliveryDetails(postcode, pickupPoint);
            this.languageId = languageId;
        }
    }

    public static class ResponseValue {
        @SerializedName("d")
        @Expose
        private final Response d;

        public ResponseValue(Response d) {
            this.d = d;
        }

        public Response getD() {
            return d;
        }

        public static class Response extends LegacyBaseResponseValue {
            @SerializedName("Value")
            @Expose
            private final Value value;

            public Response(Value value) {
                this.value = value;
            }

            public Value getValue() {
                return value;
            }
        }
    }
}
