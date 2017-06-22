package au.com.dealsdirect.ui.controller.cart;
/*
 * Created by CodeineBot on 6/22/17.
 */

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class CartPresenter<V extends CartMvpView> extends BasePresenter<V> implements CartMvpPresenter<V> {

    @Inject
    public CartPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
