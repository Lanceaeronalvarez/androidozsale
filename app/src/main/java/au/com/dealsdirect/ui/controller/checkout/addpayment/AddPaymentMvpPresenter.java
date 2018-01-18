package au.com.dealsdirect.ui.controller.checkout.addpayment;

import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 29/06/2017.
 */

public interface AddPaymentMvpPresenter <V extends MvpView> extends MvpPresenter<V> {
    boolean isDebug();
}
