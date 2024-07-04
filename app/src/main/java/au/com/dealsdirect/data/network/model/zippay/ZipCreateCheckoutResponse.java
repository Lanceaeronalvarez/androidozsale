package au.com.dealsdirect.data.network.model.zippay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZipCreateCheckoutResponse {
    @SerializedName("d")
    @Expose
    private D d;

    public ZipCreateCheckoutResponse() {
    }

    public String getId() {
        if (d == null || d.value == null) {
            return null;
        }
        return d.value.id;
    }

    public String getUri() {
        if (d == null || d.value == null) {
            return null;
        }
        return d.value.uri;
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
        @SerializedName("Id")
        @Expose
        private String id;

        @SerializedName("Uri")
        @Expose
        private String uri;
    }
}
