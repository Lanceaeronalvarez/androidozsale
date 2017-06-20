package au.com.dealsdirect.ui.controller.contact.viewcontacts;

import au.com.dealsdirect.data.network.model.viewcontactitem.GetContactsResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface ViewContactsMvpView extends MvpView {

    void showContactItems(GetContactsResponse.Response myContacts);

}
