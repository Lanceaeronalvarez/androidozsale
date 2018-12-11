package au.com.dealsdirect.ui.controller.returns.returnorders;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.ui.base.MvpPresenter;

public interface ReturnOrdersMvpPresenter<V extends ReturnOrdersMvpView> extends MvpPresenter<V> {

    void loadOrders();

    void selectReturnOrderItem(au.com.dealsdirect.data.network.model.returns.returnorders.List returnOrder);

}
