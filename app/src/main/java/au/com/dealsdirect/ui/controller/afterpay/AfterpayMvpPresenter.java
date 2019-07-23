package au.com.dealsdirect.ui.controller.afterpay;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

public interface AfterpayMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
    void createAfterpayOrder();

    void payWithAfterpay(String token);

    String getCountryIso();

    String getAfterpayScriptUri();

    boolean isBusy();
}
