package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import au.com.dealsdirect.data.network.model.contacthistory.List;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContact;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/21/17.
 */

public interface ViewContactHistoryMvpView extends MvpView{

    void showContactHistory(java.util.List<List> myContactItems);

    void repliedContactSwitchView(ReplyContact replyContact);

    void refreshViewContactMessage();

    void getAttachmentId(SetAttachmentResponse setAttachmentResponse);
}
