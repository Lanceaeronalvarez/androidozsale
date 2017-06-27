
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryAddress {

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Phone")
    @Expose
    private String phone;
    @SerializedName("State")
    @Expose
    private String state;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("Suburb")
    @Expose
    private String suburb;
    @SerializedName("Postcode")
    @Expose
    private String postcode;
    @SerializedName("AddressLines")
    @Expose
    private String addressLines;
    @SerializedName("AuthToLeave")
    @Expose
    private Boolean authToLeave;
    @SerializedName("AuthComment")
    @Expose
    private String authComment;
    @SerializedName("AdditionalData")
    @Expose
    private String additionalData;

    public String getiD() {
        return iD;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getSuburb() {
        return suburb;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getAddressLines() {
        return addressLines;
    }

    public Boolean getAuthToLeave() {
        return authToLeave;
    }

    public String getAuthComment() {
        return authComment;
    }

    public String getAdditionalData() {
        return additionalData;
    }
}
