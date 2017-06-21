package au.com.dealsdirect.data.network.model.address;

/**
 * Created by smartwave on 07/01/2017.
 */

public class AddressesItem {
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
}
