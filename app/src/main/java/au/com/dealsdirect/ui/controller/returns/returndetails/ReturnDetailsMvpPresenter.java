package au.com.dealsdirect.ui.controller.returns.returndetails;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequestOld;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface ReturnDetailsMvpPresenter<V extends ReturnDetailsMvpView> extends MvpPresenter<V> {

    void loadCurrentReturnDetails(String returnId);

    void loadReturnContacts(GetContactHistoryRequest request);

    void setAttachment(SetAttachmentRequest setAttachmentRequest);

    String getUserAgent();

    String getUserCookies();

    String getEventUserId();

    void sendMessage(CreateContactRequestOld createContactRequest);

    void replyMessage(ReplyContactRequest replyContactRequest);

    int getImageLimit();
}
