package au.com.dealsdirect.ui.controller.contact.addcontact;

import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/20/17.
 */

public interface AddContactMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void loadContactUsSubjects();

    void loadContactUsOrders();

    void createNewContact(CreateContactRequest createContactRequest);

    void replyContact(ReplyContactRequest replyContactRequest);

}
