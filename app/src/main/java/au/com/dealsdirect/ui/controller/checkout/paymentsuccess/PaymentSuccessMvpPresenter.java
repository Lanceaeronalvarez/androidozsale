package au.com.dealsdirect.ui.controller.checkout.paymentsuccess;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 30/06/2017.
 */

public interface PaymentSuccessMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void incrementPayCount();

    void generateOurpay();

    void setHasUserRateApp(boolean hasUserRateApp);

    boolean getHasUserRateApp();
}
