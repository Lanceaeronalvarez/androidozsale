package au.com.dealsdirect.ui.controller.lpay;

import au.com.dealsdirect.ui.base.MvpView;

public interface LPayMvpView extends MvpView {
    void showLPayWebView(String paymentUrl);

    void hideLPayWebView();

    void showProgressIndicator();

    void hideProgressIndicator();

    void showPaymentSuccess(String address, Double price, Double shipping, String invoice, String delivery);

    void showError(String message);
}
