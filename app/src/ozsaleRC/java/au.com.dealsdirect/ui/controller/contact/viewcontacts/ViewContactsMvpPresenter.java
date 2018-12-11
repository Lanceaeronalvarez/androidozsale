package au.com.dealsdirect.ui.controller.contact.viewcontacts;

import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface ViewContactsMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void loadContacts();

    void selectContact(GetContactsResponse.ContactList contactList);
}
