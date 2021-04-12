package au.com.dealsdirect.ui.controller.returns.currentreturns;
/*
 * Created by dp on 5/15/17.
 */


import java.util.List;

import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedRequest;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturn;
import au.com.dealsdirect.ui.base.MvpView;

public interface CurrentReturnsMvpView extends MvpView {

    void showCurrentReturns(List<CurrentReturn> currentReturnResponse);

    void returnSatisfactionReceived(ReturnReceivedRequest request, boolean hasSetSatisfactionAlready);
}
