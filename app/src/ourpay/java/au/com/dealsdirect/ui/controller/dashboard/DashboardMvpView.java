package au.com.dealsdirect.ui.controller.dashboard;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.Payment;
import au.com.dealsdirect.ui.base.MvpView;

public interface DashboardMvpView extends MvpView {

    void showPaymentDetailsController(Payment payment);
}
