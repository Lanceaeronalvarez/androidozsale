package au.com.dealsdirect.ui.controller.klarna;

import au.com.dealsdirect.data.network.model.checkout.klarna.KlarnaCreateOrderResponse;
import au.com.dealsdirect.data.network.model.checkout.klarna.KlarnaCreateSessionResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface KlarnaMvpView extends MvpView {
    void showKlarnaPaymentView(KlarnaCreateSessionResponse response);

    void hideKlarnaPaymentView();

    void showProgressIndicator();

    void hideProgressIndicator();

    void showPaymentSuccess(String address, Double price, Double shipping, String invoice, String delivery);

    void showError(String message);
}
