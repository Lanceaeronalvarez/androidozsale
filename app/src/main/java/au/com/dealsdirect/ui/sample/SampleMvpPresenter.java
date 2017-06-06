package au.com.dealsdirect.ui.sample;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.SampleRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface SampleMvpPresenter<V extends SampleMvpView> extends MvpPresenter<V> {

    void loadSample(SampleRequest request);

}
