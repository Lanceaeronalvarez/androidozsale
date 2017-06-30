package au.com.dealsdirect.ui.controller.returns.returndetails;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.ui.base.MvpPresenter;

public interface ReturnDetailsMvpPresenter<V extends ReturnDetailsMvpView> extends MvpPresenter<V> {

    void loadCurrentReturnDetails(String returnId);
}
