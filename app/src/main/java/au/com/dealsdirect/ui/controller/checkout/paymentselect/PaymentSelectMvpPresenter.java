package au.com.dealsdirect.ui.controller.checkout.paymentselect;

import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 30/06/2017.
 */

public interface PaymentSelectMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void fetchUserPaymentMethods();

    void removeUserPaymentMethod(PaymentMethod paymentMethod);
}
