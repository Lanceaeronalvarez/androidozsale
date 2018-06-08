package au.com.dealsdirect.ui.controller.checkout.addpayment;

import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 29/06/2017.
 */

public interface AddPaymentMvpPresenter <V extends MvpView> extends MvpPresenter<V> {

    void generateOurpay(Value value);

    boolean isDebug();

    void facebookInitiatedCheckout(String paymentType,
                                   int numItems,
                                   double price);

    boolean isMasterPassEnabled();
}
