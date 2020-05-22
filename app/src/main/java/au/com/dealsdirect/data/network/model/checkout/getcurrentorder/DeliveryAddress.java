package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.network.model.address.AddressesItem;


public class DeliveryAddress {
    @SerializedName(value = "ID", alternate = {"id"})
    public String id;
    @SerializedName(value = "Name", alternate = {"name"})
    public String name;
    @SerializedName(value = "Phone", alternate = {"phone"})
    public String phone;
    @SerializedName(value = "State", alternate = {"state"})
    public String state;
    @SerializedName(value = "City", alternate = {"city"})
    public String city;
    @SerializedName(value = "Suburb", alternate = {"suburb"})
    public String suburb;
    @SerializedName(value = "Postcode", alternate = {"postcode"})
    public String postcode;
    @SerializedName(value = "AddressLines", alternate = {"addressLines"})
    public String addressLines;
    @SerializedName(value = "AuthToLeave", alternate = {"authToLeave"})
    public Boolean authToLeave;
    @SerializedName(value = "AuthComment", alternate = {"authComment"})
    public String authComment;
    @SerializedName(value = "AdditionalData", alternate = {"additionalData"})
    public String additionalData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getAddressLines() {
        return addressLines;
    }

    public void setAddressLines(String addressLines) {
        this.addressLines = addressLines;
    }

    public Boolean getAuthToLeave() {
        return authToLeave;
    }

    public void setAuthToLeave(Boolean authToLeave) {
        this.authToLeave = authToLeave;
    }

    public String getAuthComment() {
        return authComment;
    }

    public void setAuthComment(String authComment) {
        this.authComment = authComment;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public boolean equalsAddressItem(AddressesItem item) {
        return getName().equalsIgnoreCase(item.Name != null ? item.Name : "") &&
                getAddressLines().equalsIgnoreCase(item.AddressLines != null ? item.AddressLines : "") &&
                getSuburb().equalsIgnoreCase(item.Suburb != null ? item.Suburb : "") &&
                getCity().equalsIgnoreCase(item.City != null ? item.City : "") &&
                getState().equalsIgnoreCase(item.State != null ? item.State : "") &&
                getPostcode().equalsIgnoreCase(item.Postcode != null ? item.Postcode : "") &&
                getPhone().equalsIgnoreCase(item.Phone != null ? item.Phone : "");
    }

    public void resetDataFromAddressItem(AddressesItem addressesItem){
        setName(addressesItem.Name != null ? addressesItem.Name : "");
        setAddressLines(addressesItem.AddressLines != null ? addressesItem.AddressLines : "");
        setSuburb(addressesItem.Suburb != null ? addressesItem.Suburb : "");
        setCity(addressesItem.City != null ? addressesItem.City : "");
        setState(addressesItem.State != null ? addressesItem.State : "");
        setPostcode(addressesItem.Postcode != null ? addressesItem.Postcode : "");
        setPhone(addressesItem.Phone != null ? addressesItem.Phone : "");
    }
}
