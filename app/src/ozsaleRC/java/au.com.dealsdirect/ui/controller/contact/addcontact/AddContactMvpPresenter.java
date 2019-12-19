package au.com.dealsdirect.ui.controller.contact.addcontact;

import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/20/17.
 */

public interface AddContactMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
    void createNewContact(CreateContactRequest createContactRequest);
    void setAttachment(SetAttachmentRequest request, boolean hasUploadedImage);
    int getImageLimit();
    String getUserAgent();
    void loadContactHistory(GetContactHistoryRequest contactHistoryRequest);
}
