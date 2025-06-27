package au.com.dealsdirect.ui.controller.checkout.addpayment;

import au.com.dealsdirect.ui.base.MvpView;

public interface AddPaymentMvpView extends MvpView {

    void onPaypalSubmit();

    void showAddPaymentResult(boolean result, String paymentType);

    void onMasterpassButtonClick();
}
