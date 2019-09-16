package au.com.dealsdirect.ui.controller.returns.newreturn;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface NewReturnMvpPresenter<V extends NewReturnMvpView> extends MvpPresenter<V> {

    void addNewReturnOrderRequest(CreateReturnRequest createReturnRequest);

    void getReturnOrderDetail(int invoiceNo);

    void updateReturnValue(String itemId, int position, int productQuantityValue,
                           boolean isChecked, String productName);

    void setAttachment(SetAttachmentRequest setAttachmentRequest, boolean hasUploadedImage);

    String getUserAgent();

    String getEventUser();

}
