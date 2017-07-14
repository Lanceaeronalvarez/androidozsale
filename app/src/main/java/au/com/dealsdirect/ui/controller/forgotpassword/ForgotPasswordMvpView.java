package au.com.dealsdirect.ui.controller.forgotpassword;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface ForgotPasswordMvpView extends MvpView {

    void showSample(SampleResponse response);
}
