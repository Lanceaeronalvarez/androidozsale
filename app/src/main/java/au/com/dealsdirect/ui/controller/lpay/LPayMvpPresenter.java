package au.com.dealsdirect.ui.controller.lpay;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

public interface LPayMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
    void createLPayOrder(String redirectUri);

    void confirmLPayTransaction(String token, String signature, String reference);

    boolean isBusy();
}
