package au.com.dealsdirect.ui.controller.returns.currentreturns;
/*
 * Created by dp on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturnResponseBody;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponseBody;
import au.com.dealsdirect.ui.base.MvpView;

public interface CurrentReturnsMvpView extends MvpView {

    void showCurrentReturns(CurrentReturnResponseBody currentReturnResponseBody);

    void showCurrentReturnDetails(GetReturnDetailsResponseBody getReturnDetailsResponseBody);
}
