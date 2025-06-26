package au.com.dealsdirect.ui.controller.checkout.addpayment;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;

public interface AddPaymentMvpPresenter <V extends MvpView> extends MvpPresenter<V> {

    boolean isDebug();

    void facebookInitiatedCheckout(String paymentType,
                                   int numItems,
                                   double price);

    boolean isMasterPassEnabled();

    boolean isPaypalCreditEnabled();

    boolean isPayPalEnabled();
}
