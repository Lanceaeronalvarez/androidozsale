package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/21/17.
 */

public interface ViewContactHistoryMvpView extends MvpView {

    void showContactHistory(GetContactHistoryResponse myContactItems);

    void repliedContactSwitchView(String response);

    void refreshViewContactMessage();

    void setAttachmentId(String attachmentId);
}
