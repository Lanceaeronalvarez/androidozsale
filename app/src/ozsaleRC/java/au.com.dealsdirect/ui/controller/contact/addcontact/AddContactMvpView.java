package au.com.dealsdirect.ui.controller.contact.addcontact;

import au.com.dealsdirect.data.network.model.contactorder.ContactOrderList;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContact;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactResponse;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentResponse;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.data.network.model.contacthistory.List;

/**
 * dp Created by Admin on 6/20/17.
 */

public interface AddContactMvpView extends MvpView {

    void contactCreatedSwitchView(CreateContactResponse createContactResponse);
    void getAttachmentId(SetAttachmentResponse setAttachmentResponse);
    void showViewContactHistory();
    void showContactSuccess(java.util.List<List> myContactItems);
}
