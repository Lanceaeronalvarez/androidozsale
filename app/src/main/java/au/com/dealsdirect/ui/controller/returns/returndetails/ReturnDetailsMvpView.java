package au.com.dealsdirect.ui.controller.returns.returndetails;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponseBody;
import au.com.dealsdirect.ui.base.MvpView;

public interface ReturnDetailsMvpView extends MvpView {

    void showCurrentReturnDetails(GetReturnDetailsResponseBody getReturnDetailsResponseBody);
}
