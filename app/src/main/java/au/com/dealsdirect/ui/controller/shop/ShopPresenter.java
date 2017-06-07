package au.com.dealsdirect.ui.controller.shop;
/*
 * Created by CodeineBot on 5/15/17.
 */


import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.SampleRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class ShopPresenter<V extends ShopMvpView> extends BasePresenter<V> implements
        ShopMvpPresenter<V> {

    @Inject
    public ShopPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override public void loadSample(SampleRequest request) {

    }
}
