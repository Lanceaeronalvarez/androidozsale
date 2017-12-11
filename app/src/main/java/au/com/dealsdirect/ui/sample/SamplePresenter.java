package au.com.dealsdirect.ui.sample;
/*
 * Created by CodeineBot on 5/15/17.
 */


import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.SampleRequest;
import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class SamplePresenter<V extends SampleMvpView> extends BasePresenter<V> implements SampleMvpPresenter<V> {

    @Inject
    public SamplePresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadSample(SampleRequest request) {
        doApiCallForResponse(getDataManager().callGetServerSettings("DA"), new AppApiCallback() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess();
                getMvpView().showSample((SampleResponse) o);
            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
            }
        });
    }
}
