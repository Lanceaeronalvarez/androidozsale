package au.com.dealsdirect.data.network.model.contactitem;

import java.util.List;

/**
 * dp Created by Admin on 1/9/17.
 */

public class ContactItemByDate {

    private String dateHeaderFormat;

    private List<GetContactsResponse.ContactList> contactItemList;

    public List<GetContactsResponse.ContactList> getContactItemList() {
        return contactItemList;
    }

    public void setContactItemList(List<GetContactsResponse.ContactList> contactItemList) {
        this.contactItemList = contactItemList;
    }

    public String getDateHeaderFormat() {
        return dateHeaderFormat;
    }

    public void setDateHeaderFormat(String dateHeaderFormat) {
        this.dateHeaderFormat = dateHeaderFormat;
    }
}
