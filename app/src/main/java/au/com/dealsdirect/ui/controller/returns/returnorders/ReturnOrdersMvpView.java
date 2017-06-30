package au.com.dealsdirect.ui.controller.returns.returnorders;
/*
 * Created by CodeineBot on 5/15/17.
 */


import java.util.List;

import au.com.dealsdirect.ui.base.MvpView;

public interface ReturnOrdersMvpView extends MvpView {

    void showOrders(List<au.com.dealsdirect.data.network.model.returns.returnorders.List> getReturnOrder);
}
