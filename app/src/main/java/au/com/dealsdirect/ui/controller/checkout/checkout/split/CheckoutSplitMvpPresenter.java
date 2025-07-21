package au.com.dealsdirect.ui.controller.checkout.checkout.split;

import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

public interface CheckoutSplitMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    CartDetailsMapper getCart();
}
