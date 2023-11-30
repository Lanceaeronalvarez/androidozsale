package au.com.dealsdirect.ui.controller.returns.returndetails;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedRequest;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturn;
import au.com.dealsdirect.ui.base.MvpView;

public interface ReturnDetailsMvpView extends MvpView {

    void showCurrentReturnDetails(CurrentReturn item);

    void showContactMessageReturn(GetContactHistoryResponse responseValue);

    void refreshReturnDetails(String setAttachmentResponse);

    void getImageUrl(String imageUrl);

    void finishedSendMessage(String response);

    void returnSatisfactionReceived(ReturnReceivedRequest request, boolean hasSetSatisfactionAlready);
}
