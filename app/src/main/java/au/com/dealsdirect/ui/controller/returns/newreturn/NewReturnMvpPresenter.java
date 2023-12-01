package au.com.dealsdirect.ui.controller.returns.newreturn;
/*
 * Created by CodeineBot on 5/15/17.
 */


import java.util.List;

import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.ImageAttachment;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface NewReturnMvpPresenter<V extends NewReturnMvpView> extends MvpPresenter<V> {

    void addNewReturnOrderRequest(CreateReturnRequest createReturnRequest);

    void getReturnOrderDetail(int invoiceNo);

    void setAttachment(String returnId, List<ImageAttachment> setAttachmentRequest, boolean hasUploadedImage);

    String getUserAgent();

    String getEventUser();

    int getImageLimit();

}
