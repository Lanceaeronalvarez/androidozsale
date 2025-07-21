package au.com.dealsdirect.ui.controller.checkout.addpayment;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

public interface AddPaymentMvpPresenter <V extends MvpView> extends MvpPresenter<V> {

    void facebookInitiatedCheckout(String paymentType,
                                   int numItems,
                                   double price);

    boolean isMasterPassEnabled();

    boolean isPaypalCreditEnabled();

    boolean isPayPalEnabled();

    boolean isStripe();
}
