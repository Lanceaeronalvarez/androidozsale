package au.com.dealsdirect.ui.controller.returns.newreturn;
/*
 * Created by CodeineBot on 5/15/17.
 */


import java.util.List;

import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponse;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnItem;
import au.com.dealsdirect.ui.base.MvpView;

public interface NewReturnMvpView extends MvpView {

    void finishCreateReturnRequest(CreateReturnRequestResponse createReturnRequestResponse);

    void loadReturnOrderDetail(List<NewReturnItem> newReturnsOrderDetail);

    void getAttachmentId(String setAttachmentResponse);

    void getImageUrl(String imageUrl);

    void finishReturnRequestTransaction();
}
