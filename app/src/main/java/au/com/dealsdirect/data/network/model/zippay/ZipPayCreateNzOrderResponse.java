package au.com.dealsdirect.data.network.model.zippay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZipPayCreateNzOrderResponse {
    @SerializedName("d")
    @Expose
    private D d;

    public ZipPayCreateNzOrderResponse() {
    }

    public String getToken() {
        if (d == null || d.value == null) {
            return null;
        }
        return d.value.token;
    }

    public String getRedirectUrl() {
        if (d == null || d.value == null) {
            return null;
        }
        return d.value.redirectUrl;
    }

    public String getOrderId() {
        if (d == null || d.value == null) {
            return null;
        }
        return d.value.orderId;
    }

    public static class D {
        @SerializedName("IsAuthenticated")
        @Expose
        private boolean isAuthenticated;

        @SerializedName("Value")
        @Expose
        private Value value;

        @SerializedName("Result")
        @Expose
        private boolean result;

        @SerializedName("Message")
        @Expose
        private String message;
    }

    public static class Value {
        @SerializedName("Token")
        @Expose
        private String token;

        @SerializedName("RedirectUrl")
        @Expose
        private String redirectUrl;

        @SerializedName("OrderId")
        @Expose
        private String orderId;
    }
}
