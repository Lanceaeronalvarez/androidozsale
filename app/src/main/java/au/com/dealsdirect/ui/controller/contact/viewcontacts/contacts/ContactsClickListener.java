package au.com.dealsdirect.ui.controller.contact.viewcontacts.contacts;

import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;

/**
 * dp Created by Admin on 6/20/17.
 */

public interface ContactsClickListener {

    void onContactClicked(GetContactsResponse contactOrder);

}
