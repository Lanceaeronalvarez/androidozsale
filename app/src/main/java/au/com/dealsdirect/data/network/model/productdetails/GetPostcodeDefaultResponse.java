package au.com.dealsdirect.data.network.model.productdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPostcodeDefaultResponse {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("subscribed")
    @Expose
    private boolean subscribed;
    @SerializedName("address")
    @Expose
    private Address address;

    public String getId() {
        return id;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public Address getAddress() {
        return address;
    }

    public String getPostcode() {
        return address != null ? getAddress().getPostcode() : null;
    }

    public static class Address {
        @SerializedName("postcode")
        @Expose
        private String postcode;

        public String getPostcode() {
            return postcode;
        }
    }
}
