package au.com.dealsdirect.data.network.model.address;

/**
 * Created by smartwave on 07/01/2017.
 */

public class AddressesItem {
    public int addressNumericId;
    public String ID;
    public String CustomerID;
    public String Nickname;
    public String Name;
    public String Phone;
    public String State;
    public String City;
    public String Suburb;
    public String Postcode;
    public String AddressLines;
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
        String addressDesc = AddressLines + ", ";
        if (Suburb != null) {
            addressDesc = addressDesc + Suburb + ", ";
        }
        if (City != null) {
            addressDesc = addressDesc + City + ", ";
        }
        if (State != null) {
            addressDesc = addressDesc + State + ", ";
        }
        addressDesc = addressDesc + Postcode + ", " + Phone ;

        return addressDesc;
    }

    public String getAddressName(){
        return Name;
    }

    public String getAddressId(){ return ID; }

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
        if (ID != null ? !ID.equals(that.ID) : that.ID != null) return false;
        if (CustomerID != null ? !CustomerID.equals(that.CustomerID) : that.CustomerID != null)
            return false;
        if (Nickname != null ? !Nickname.equals(that.Nickname) : that.Nickname != null)
            return false;
        if (Name != null ? !Name.equals(that.Name) : that.Name != null) return false;
        if (Phone != null ? !Phone.equals(that.Phone) : that.Phone != null) return false;
        if (State != null ? !State.equals(that.State) : that.State != null) return false;
        if (City != null ? !City.equals(that.City) : that.City != null) return false;
        if (Suburb != null ? !Suburb.equals(that.Suburb) : that.Suburb != null) return false;
        if (Postcode != null ? !Postcode.equals(that.Postcode) : that.Postcode != null)
            return false;
        if (AddressLines != null ? !AddressLines.equals(that.AddressLines) : that.AddressLines != null)
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
