package au.com.dealsdirect.ui.controller.checkout.paymentselect;

import java.util.List;

import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 30/06/2017.
 */

public interface PaymentSelectMvpView extends MvpView {

    void showPaymentList(List<PaymentMethod> paymentMethods);

    void showRemovePaymentMethodResult(PaymentMethod paymentMethod, boolean result, String message);
}
