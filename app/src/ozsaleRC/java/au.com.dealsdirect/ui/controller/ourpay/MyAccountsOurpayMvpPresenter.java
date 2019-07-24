package au.com.dealsdirect.ui.controller.ourpay;

import android.app.Activity;

import au.com.dealsdirect.data.network.model.ourpaydata.ProcessOurpayInstallmentRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

public interface MyAccountsOurpayMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
    void fetchDataForSummary();

    void fetchDataForPaymentPlans();

    void fetchDataForScheduledPayments();

    void fetchDataForPastPayments();

    void processOurpayInstallment(ProcessOurpayInstallmentRequest request);
}
