package au.com.dealsdirect.data.network.model.address;

/**
 * Created by smartwave on 07/01/2017.
 */

public class AddressesItem {
    public int addressNumericId;
    public String id;
    public String CustomerID;
    public String Nickname;
    public String name;
    public String phone;
    public String state;
    public String City;
    public String suburb;
    public String postcode;
    public String address_lines;
    public boolean PayPalAddress;
    public String ListOrder;
    public String CreatedDate;
    public String CreatedBy;
    public String LastSavedDate;
    public String LastSavedBy;
    public boolean IsDeleted;
    public boolean AuthToLeave;
    public String AuthComment;
    public String AdditionalData;
    public int Type;
    public boolean HasApprovedOrder;
    public boolean ReadOnly;
    public int Status;
    public boolean IsChanged;
    private boolean isPinned;

    public String getFullAddress(){
        String addressDesc = address_lines + ", ";
        if (suburb != null) {
            addressDesc = addressDesc + suburb + ", ";
        }
        if (City != null) {
            addressDesc = addressDesc + City + ", ";
        }
        if (state != null) {
            addressDesc = addressDesc + state + ", ";
        }
        addressDesc = addressDesc + postcode + ", " + phone ;

        return addressDesc;
    }

    public String getAddressName(){
        return name;
    }

    public String getAddressId(){ return id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressesItem that = (AddressesItem) o;

        if (PayPalAddress != that.PayPalAddress) return false;
        if (IsDeleted != that.IsDeleted) return false;
        if (AuthToLeave != that.AuthToLeave) return false;
        if (Type != that.Type) return false;
        if (HasApprovedOrder != that.HasApprovedOrder) return false;
        if (ReadOnly != that.ReadOnly) return false;
        if (Status != that.Status) return false;
        if (IsChanged != that.IsChanged) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (CustomerID != null ? !CustomerID.equals(that.CustomerID) : that.CustomerID != null)
            return false;
        if (Nickname != null ? !Nickname.equals(that.Nickname) : that.Nickname != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (City != null ? !City.equals(that.City) : that.City != null) return false;
        if (suburb != null ? !suburb.equals(that.suburb) : that.suburb != null) return false;
        if (postcode != null ? !postcode.equals(that.postcode) : that.postcode != null)
            return false;
        if (address_lines != null ? !address_lines.equals(that.address_lines) : that.address_lines != null)
            return false;
        if (ListOrder != null ? !ListOrder.equals(that.ListOrder) : that.ListOrder != null)
            return false;
        if (CreatedDate != null ? !CreatedDate.equals(that.CreatedDate) : that.CreatedDate != null)
            return false;
        if (CreatedBy != null ? !CreatedBy.equals(that.CreatedBy) : that.CreatedBy != null)
            return false;
        if (LastSavedDate != null ? !LastSavedDate.equals(that.LastSavedDate) : that.LastSavedDate != null)
            return false;
        if (LastSavedBy != null ? !LastSavedBy.equals(that.LastSavedBy) : that.LastSavedBy != null)
            return false;
        if (AuthComment != null ? !AuthComment.equals(that.AuthComment) : that.AuthComment != null)
            return false;
        return AdditionalData != null ? AdditionalData.equals(that.AdditionalData) : that.AdditionalData == null;

    }
    public int getAddressNumericId() {
        return addressNumericId;
    }

    public void setAddressNumericId(int addressNumericId) {
        this.addressNumericId = addressNumericId;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
}
