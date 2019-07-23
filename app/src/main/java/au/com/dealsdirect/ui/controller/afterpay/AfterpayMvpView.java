package au.com.dealsdirect.ui.controller.afterpay;

import au.com.dealsdirect.ui.base.MvpView;

public interface AfterpayMvpView extends MvpView {
    void showAfterpayWebView(String token);

    void hideAfterpayWebView();

    void showProgressIndicator();

    void hideProgressIndicator();

    void showPaymentSuccess(String address, Double price, Double shipping, String invoice, String delivery);

    void showError(String message);
}
