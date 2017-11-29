package au.com.dealsdirect.ui.controller.dashboard.details;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.widget.ImageView;
import android.widget.TextView;

import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.PlannedTransaction;
import au.com.dealsdirect.ui.base.MvpView;

public interface PaymentDetailsMvpView extends MvpView {

    void populatePaymentSchedule();

    void populatePaymentScheduleCell(PlannedTransaction transaction, ImageView circle, TextView date, TextView value);
}
