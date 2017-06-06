package au.com.dealsdirect.ui.sample;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface SampleMvpView extends MvpView {

    void showSample(SampleResponse response);
}
