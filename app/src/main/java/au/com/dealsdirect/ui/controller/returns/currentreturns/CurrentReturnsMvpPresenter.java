package au.com.dealsdirect.ui.controller.returns.currentreturns;
/*
 * Created by dp on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface CurrentReturnsMvpPresenter<V extends CurrentReturnsMvpView> extends MvpPresenter<V> {

    void loadCurrentReturns();

    void loadReturnDetails(GetReturnDetailRequest getReturnDetailRequest);

    void currentReturnSelected(
            int orderNumber,
            int position,
            String productName,
            String productRequestStatus,
            String productRAN,
            String returnRequestDateFormat,
            String isRequestApproved,
            String returnId);

}
