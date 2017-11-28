package au.com.dealsdirect.ui.controller.dashboard;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.ui.base.MvpPresenter;

public interface DashboardMvpPresenter<V extends DashboardMvpView> extends MvpPresenter<V> {

    void getPaymentPlans();

    void getScheduledPayments();

    void getPastPayments();

    void getDeliveryService();
}
