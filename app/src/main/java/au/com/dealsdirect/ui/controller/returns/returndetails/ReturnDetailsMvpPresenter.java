package au.com.dealsdirect.ui.controller.returns.returndetails;
/*
 * Created by CodeineBot on 5/15/17.
 */


import java.util.List;

import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.ImageAttachment;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface ReturnDetailsMvpPresenter<V extends ReturnDetailsMvpView> extends MvpPresenter<V> {

    void loadCurrentReturnDetails(String returnId);

    void loadReturnContacts(GetContactHistoryRequest request);

    void setAttachment(String returnId, List<ImageAttachment> setAttachmentRequest);

    String getUserAgent();

    String getUserCookies();

    String getEventUserId();

    void sendMessage(String returnId, CreateContactRequest createContactRequest);

    void replyMessage(ReplyContactRequest replyContactRequest);

    int getImageLimit();

    void callSetReturnReceived(ReturnReceivedRequest receivedRequest);

    void callSetReturnNotReceived(ReturnReceivedRequest receivedRequest);

    void callGetReturnReceivedSatisfaction(ReturnReceivedRequest receivedRequest);
}
