package au.com.dealsdirect.ui.controller.returns.newreturn;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.SampleRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface NewReturnMvpPresenter<V extends NewReturnMvpView> extends MvpPresenter<V> {

    void loadSample(SampleRequest request);

}
