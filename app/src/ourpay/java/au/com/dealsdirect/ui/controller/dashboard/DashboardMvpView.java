package au.com.dealsdirect.ui.controller.dashboard;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.ourpaydashboard.Payment;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.GetPastPaymentsResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.GetPaymentPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledpayments.GetScheduledPaymentsResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface DashboardMvpView extends MvpView {

    void showPaymentPlans(GetPaymentPlansResponse paymentPlansResponse);

    void showScheduledPayments(GetScheduledPaymentsResponse scheduledPaymentsResponse);

    void showPastPayments(GetPastPaymentsResponse pastPaymentsResponse);

    void refreshPager();

    void showPaymentDetailsController(Payment payment);
}
