package au.com.dealsdirect.ui.controller.ourpay;

import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.GetPastPaymentsResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.GetPaymentPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.GetScheduledPlansResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface MyAccountsOurpayMvpView extends MvpView {
    void showEmptyView();
    void showTabbedView();

    void setDataForSummary(MyAccountsOurpayDataSource.Summary summary);

    void setDataForPaymentPlans(GetPaymentPlansResponse response);

    void setDataForScheduledPayments(GetScheduledPlansResponse response);

    void setDataForPastPayments(GetPastPaymentsResponse response);
}
