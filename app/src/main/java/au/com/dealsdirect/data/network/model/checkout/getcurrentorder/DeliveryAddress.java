package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.network.model.address.AddressesItem;


public class DeliveryAddress implements Parcelable {
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

    protected DeliveryAddress(Parcel in) {
        id = in.readString();
        name = in.readString();
        phone = in.readString();
        state = in.readString();
        city = in.readString();
        suburb = in.readString();
        postcode = in.readString();
        addressLines = in.readString();
        authComment = in.readString();
        additionalData = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(state);
        dest.writeString(city);
        dest.writeString(suburb);
        dest.writeString(postcode);
        dest.writeString(addressLines);
        dest.writeString(authComment);
        dest.writeString(additionalData);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeliveryAddress> CREATOR = new Creator<DeliveryAddress>() {
        @Override
        public DeliveryAddress createFromParcel(Parcel in) {
            return new DeliveryAddress(in);
        }

        @Override
        public DeliveryAddress[] newArray(int size) {
            return new DeliveryAddress[size];
        }
    };

    public boolean equalsAddressItem(AddressesItem item) {
        return name.equalsIgnoreCase(item.Name != null ? item.Name : "") &&
                addressLines.equalsIgnoreCase(item.AddressLines != null ? item.AddressLines : "") &&
                suburb.equalsIgnoreCase(item.Suburb != null ? item.Suburb : "") &&
                city.equalsIgnoreCase(item.City != null ? item.City : "") &&
                state.equalsIgnoreCase(item.State != null ? item.State : "") &&
                postcode.equalsIgnoreCase(item.Postcode != null ? item.Postcode : "") &&
                phone.equalsIgnoreCase(item.Phone != null ? item.Phone : "");
    }

    public void resetDataFromAddressItem(AddressesItem addressesItem){
        this.name = addressesItem.Name != null ? addressesItem.Name : "";
        this.addressLines = addressesItem.AddressLines != null ? addressesItem.AddressLines : "";
        this.suburb = addressesItem.Suburb != null ? addressesItem.Suburb : "";
        this.city = addressesItem.City != null ? addressesItem.City : "";
        this.state = addressesItem.State != null ? addressesItem.State : "";
        this.postcode = addressesItem.Postcode != null ? addressesItem.Postcode : "";
        this.phone = addressesItem.Phone != null ? addressesItem.Phone : "";
    }
}
