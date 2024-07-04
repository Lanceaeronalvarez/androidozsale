package au.com.dealsdirect.ui.controller.zippay;

import java.util.Map;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

public interface ZipPayMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void createOrder(String redirectUrl);

    void createAUCharge(String checkoutId, String zipOrderId, String paymentStatus);

    void confirmNZOrder(String paymentStatus, String zipOrderId, String token);
    boolean isBusy();
}
