package au.com.dealsdirect.ui.controller.checkout.checkout.steps.payment;

import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

public interface CheckoutStepsPaymentMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    CartDetailsMapper getCart();
}
