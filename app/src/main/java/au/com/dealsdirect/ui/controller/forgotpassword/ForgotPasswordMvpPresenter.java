package au.com.dealsdirect.ui.controller.forgotpassword;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.SampleRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface ForgotPasswordMvpPresenter<V extends ForgotPasswordMvpView> extends MvpPresenter<V> {

    void loadSample(SampleRequest request);

}
