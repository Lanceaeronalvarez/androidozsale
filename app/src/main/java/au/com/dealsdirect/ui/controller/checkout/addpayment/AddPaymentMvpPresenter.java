package au.com.dealsdirect.ui.controller.checkout.addpayment;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;

/**
 * Created by smartwave on 29/06/2017.
 */

public interface AddPaymentMvpPresenter <V extends MvpView> extends MvpPresenter<V> {

    void generateOurpay(CheckoutDetailsMapper value);

    boolean isDebug();

    void facebookInitiatedCheckout(String paymentType,
                                   int numItems,
                                   double price);

    boolean isMasterPassEnabled();

    boolean isPaypalCreditEnabled();

    boolean isPayPalEnabled();
}
