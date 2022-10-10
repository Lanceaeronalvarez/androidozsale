package au.com.dealsdirect.ui.controller.openpay;

import au.com.dealsdirect.ui.base.MvpView;

public interface OpenpayMvpView extends MvpView {
    void showOpenpayWebView(String planId, String orderId, String postUrl);

    void hideOpenpayWebView();

    void showProgressIndicator();

    void hideProgressIndicator();

    void showPaymentSuccess(String address, Double price, Double shipping, String invoice, String delivery);

    void showError(String message);
}
