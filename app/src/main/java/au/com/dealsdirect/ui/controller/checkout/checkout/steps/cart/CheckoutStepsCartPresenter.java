package au.com.dealsdirect.ui.controller.checkout.checkout.steps.cart;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class CheckoutStepsCartPresenter<V extends CheckoutStepsCartMvpView> extends BasePresenter<V> implements CheckoutStepsCartMvpPresenter<V> {

    @Inject
    public CheckoutStepsCartPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public CartDetailsMapper getCart() {
        return null;
    }
}
