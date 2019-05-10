package au.com.dealsdirect.ui.controller.ourpay;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

public interface MyAccountsOurpayMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
    void fetchDataForSummary();

    void fetchDataForPaymentPlans();

    void fetchDataForScheduledPayments();

    void fetchDataForPastPayments();
}
