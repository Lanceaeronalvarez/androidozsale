package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

/**
 * dp Created by Admin on 6/21/17.
 */

public interface ViewContactHistoryMvpPresenter<V extends ViewContactHistoryMvpView> extends MvpPresenter<V> {

    void loadContactHistory(GetContactHistoryRequest contactHistoryRequest);

    void replyContact(ReplyContactRequest replyContactRequest);

    int getImageLimit();

    String getUserAgent();

    void setAttachment(SetAttachmentRequest setAttachmentRequest, boolean hasUploadedImage);
}
