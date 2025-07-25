package au.com.dealsdirect.ui.controller.checkout.checkout.steps.contact;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class CheckoutStepsContactPresenter<V extends CheckoutStepsContactMvpView> extends BasePresenter<V> implements CheckoutStepsContactMvpPresenter<V> {

    @Inject
    public CheckoutStepsContactPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public CartDetailsMapper getCart() {
        return getDataManager().getCart();
    }
}
