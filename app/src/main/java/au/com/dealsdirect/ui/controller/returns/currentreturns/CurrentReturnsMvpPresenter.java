package au.com.dealsdirect.ui.controller.returns.currentreturns;
/*
 * Created by dp on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface CurrentReturnsMvpPresenter<V extends CurrentReturnsMvpView> extends MvpPresenter<V> {

    void loadCurrentReturns();

    void callSetReturnReceived(ReturnReceivedRequest receivedRequest);

    void callSetReturnNotReceived(ReturnReceivedRequest receivedRequest);

    void callGetReturnReceivedSatisfaction(ReturnReceivedRequest receivedRequest);
}
