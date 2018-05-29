package au.com.dealsdirect.ui.controller.contact.addcontact;

import java.util.List;

import au.com.dealsdirect.data.network.model.contactorder.ContactOrderList;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContact;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/20/17.
 */

public interface AddContactMvpView extends MvpView {

    void showContactFirstSubject(List<String> contactSubjectList);

    void showContactFirstOrder(List<ContactOrderList> contactOrderList);

    void contactCreatedSwitchView(CreateContactResponse createContactResponse);

    void repliedContactSwitchView(ReplyContact replyContact);
}
