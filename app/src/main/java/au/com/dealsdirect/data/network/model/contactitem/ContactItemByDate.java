package au.com.dealsdirect.data.network.model.contactitem;

import java.util.List;

/**
 * dp Created by Admin on 1/9/17.
 */

public class ContactItemByDate {

    private String dateHeaderFormat;

    private List<GetContactsResponse> contactItemList;

    public List<GetContactsResponse> getContactItemList() {
        return contactItemList;
    }

    public void setContactItemList(List<GetContactsResponse> contactItemList) {
        this.contactItemList = contactItemList;
    }

    public String getDateHeaderFormat() {
        return dateHeaderFormat;
    }

    public void setDateHeaderFormat(String dateHeaderFormat) {
        this.dateHeaderFormat = dateHeaderFormat;
    }
}
