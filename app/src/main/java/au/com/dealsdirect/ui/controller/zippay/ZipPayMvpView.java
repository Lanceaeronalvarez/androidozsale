package au.com.dealsdirect.ui.controller.zippay;

import au.com.dealsdirect.ui.base.MvpView;

public interface ZipPayMvpView extends MvpView {
    void showProgressIndicator();

    void hideProgressIndicator();

    void showPaymentSuccess(String address, Double price, Double shipping, String invoice, String delivery);

    void showError(String message);

    void receivedAUOrderRequest(String redirectUri);

    void receivedNZOrderRequest(String redirectUri);
}
