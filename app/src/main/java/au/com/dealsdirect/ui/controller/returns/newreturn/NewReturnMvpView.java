package au.com.dealsdirect.ui.controller.returns.newreturn;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface NewReturnMvpView extends MvpView {

    void showSample(SampleResponse response);
}
