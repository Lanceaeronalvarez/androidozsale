package au.com.dealsdirect.ui.controller.returns.newreturn;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponseBody;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface NewReturnMvpView extends MvpView {

    void finishCreateReturnRequest(CreateReturnRequestResponseBody createReturnRequest);

    void loadReturnOrderDetail(NewReturnOrderDetailResponse newReturnsOrderDetail);

    void onReturnValueUpdated(String itemId, int position, int productQuantityValue);
}
