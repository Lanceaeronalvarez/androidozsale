package au.com.dealsdirect.ui.controller.orders.orders;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 22/06/2017.
 */

public interface OrdersMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void loadOrders();
}
