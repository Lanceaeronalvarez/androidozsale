package au.com.dealsdirect.ui.controller.openpay;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

public interface OpenpayMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
    void createOpenpayOrder();

    void capturePaymentRequest(String planId, String orderId, String status);

    String getCountryIso();

    boolean isBusy();

    String getRedirectUrlPrefix();
}
