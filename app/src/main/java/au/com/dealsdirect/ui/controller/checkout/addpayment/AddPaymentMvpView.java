package au.com.dealsdirect.ui.controller.checkout.addpayment;

import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;

/**
 * Created by smartwave on 29/06/2017.
 */

public interface AddPaymentMvpView extends MvpView {

    void onPaypalSubmit();

    void showAddPaymentResult(boolean result, String paymentType);

    void clearFields();

    void showMyPayDetails(CheckoutDetailsMapper value, Ourpay ourpay);

    void onMasterpassButtonClick();
}
