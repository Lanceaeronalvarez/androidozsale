package au.com.dealsdirect.ui.controller.checkout.checkout.steps.shipping;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class CheckoutStepsShippingPresenter<V extends CheckoutStepsShippingMvpView> extends BasePresenter<V> implements CheckoutStepsShippingMvpPresenter<V> {

    @Inject
    public CheckoutStepsShippingPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public CartDetailsMapper getCart() {
        return getDataManager().getCart();
    }
}
