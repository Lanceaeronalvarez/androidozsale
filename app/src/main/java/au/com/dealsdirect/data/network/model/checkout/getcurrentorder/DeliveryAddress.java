package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.SerializedName;

public class DeliveryAddress {
    @SerializedName("ID")
    public String id;
    @SerializedName("Name")
    public String name;
    @SerializedName("Phone")
    public String phone;
    @SerializedName("State")
    public String state;
    @SerializedName("City")
    public String city;
    @SerializedName("Suburb")
    public String suburb;
    @SerializedName("Postcode")
    public String postcode;
    @SerializedName("AddressLines")
    public String addressLines;
    @SerializedName("AuthToLeave")
    public Boolean authToLeave;
    @SerializedName("AuthComment")
    public String authComment;
    @SerializedName("AdditionalData")
    public String additionalData;
}
