package au.com.dealsdirect.ui.controller.returns.currentreturns;
/*
 * Created by dp on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturnResponseBody;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponseBody;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.returns.currentreturns.viewholder.CurrentReturnViewHolder;

public interface CurrentReturnsMvpView extends MvpView {

    void showCurrentReturns(CurrentReturnResponseBody currentReturnResponseBody);

    void showCurrentReturnDetails(GetReturnDetailsResponseBody getReturnDetailsResponseBody);

    void onCurrentReturnClickListener(int orderNumber,
                                      int position,
                                      String productName,
                                      String productRequestStatus,
                                      String productRAN,
                                      String returnRequestDateFormat,
                                      String isRequestApproved,
                                      String returnId);
}
