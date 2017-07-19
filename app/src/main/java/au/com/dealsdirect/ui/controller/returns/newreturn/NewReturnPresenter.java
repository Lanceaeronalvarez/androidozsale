package au.com.dealsdirect.ui.controller.returns.newreturn;
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

public class NewReturnPresenter<V extends NewReturnMvpView> extends BasePresenter<V> implements NewReturnMvpPresenter<V> {

    @Inject
    public NewReturnPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadSample(SampleRequest request) {
        doApiCallForObjectResponse(getDataManager().doSampleApiCall(request), new AppApiCallback() {
            @Override
            public void onSuccess(Object o) {
                getMvpView().showSample((SampleResponse) o);
            }
        });
    }
}
