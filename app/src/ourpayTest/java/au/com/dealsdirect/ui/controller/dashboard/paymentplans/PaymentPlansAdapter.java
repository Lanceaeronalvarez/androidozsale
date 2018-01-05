package au.com.dealsdirect.ui.controller.dashboard.paymentplans;
/*
 * Created by CodeineBot on 6/7/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.PaymentPlan;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.PlannedTransaction;
import au.com.dealsdirect.ui.controller.dashboard.DashboardController;
import au.com.dealsdirect.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentPlansAdapter extends RecyclerView.Adapter<PaymentPlansAdapter.ViewHolder> {

    Context mContext;
    DashboardController mController;
    List<PaymentPlan> mData;
    String lastState;

    public PaymentPlansAdapter(DashboardController controller, List<PaymentPlan> data) {
        this.mController = controller;
        this.mData = data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_payment_plan_title)
        TextView title;

        @BindView(R.id.row_payment_plan_id)
        TextView id;

        @BindView(R.id.row_payment_plan_value)
        TextView value;

        @BindView(R.id.row_payment_plan_balance)
        TextView balance;

        @BindView(R.id.row_payment_plan_schedule_circle1)
        ImageView circle1;

        @BindView(R.id.row_payment_plan_schedule_circle2)
        ImageView circle2;

        @BindView(R.id.row_payment_plan_schedule_circle3)
        ImageView circle3;

        @BindView(R.id.row_payment_plan_schedule_circle4)
        ImageView circle4;

        @BindView(R.id.row_payment_plan_schedule_date1)
        TextView date1;

        @BindView(R.id.row_payment_plan_schedule_date2)
        TextView date2;

        @BindView(R.id.row_payment_plan_schedule_date3)
        TextView date3;

        @BindView(R.id.row_payment_plan_schedule_date4)
        TextView date4;

        @BindView(R.id.row_payment_plan_schedule_value1)
        TextView value1;

        @BindView(R.id.row_payment_plan_schedule_value2)
        TextView value2;

        @BindView(R.id.row_payment_plan_schedule_value3)
        TextView value3;

        @BindView(R.id.row_payment_plan_schedule_value4)
        TextView value4;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_payment_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final PaymentPlan item = mData.get(position);

        holder.title.setText(item.getName());
        holder.id.setText(item.getOrderNo());
        holder.value.setText(String.format(Locale.getDefault(), "%s%.2f", item.getCurrencySign(), item.getTotalAmount()));
        holder.balance.setText(String.format(Locale.getDefault(), "%s%.2f", item.getCurrencySign(), item.getOrderBalance()));

        List<PlannedTransaction> plannedTransactions = item.getPlannedTransactions();

        populatePaymentScheduleCell(item, plannedTransactions.get(0), holder.circle1, holder.date1, holder.value1);

        populatePaymentScheduleCell(item, plannedTransactions.get(1), holder.circle2, holder.date2, holder.value2);

        populatePaymentScheduleCell(item, plannedTransactions.get(2), holder.circle3, holder.date3, holder.value3);

        populatePaymentScheduleCell(item, plannedTransactions.get(3), holder.circle4, holder.date4, holder.value4);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void populatePaymentScheduleCell(PaymentPlan item, PlannedTransaction transaction, ImageView circle, TextView date, TextView value) {

        if (transaction.getState().equalsIgnoreCase("successful")) {
            circle.setImageResource(R.drawable.ic_schedule_successful);
        } else if (transaction.getState().equalsIgnoreCase("pending")) {
            processPendingCell(transaction, circle);
        } else if (transaction.getState().equalsIgnoreCase("cancelled")) {
            circle.setImageResource(R.drawable.ic_schedule_cancelled);
        }

        lastState = transaction.getState();

        date.setText(DateUtils.getDateFromStringInFormat(transaction.getPlannedDate(), "MMM dd").toLowerCase());

        value.setText(String.format(Locale.getDefault(), "%s%.2f", item.getCurrencySign(), transaction.getAmount()));
    }

    private void processPendingCell(PlannedTransaction transaction, ImageView circle) {
        if (lastState.equalsIgnoreCase("successful")) {
            switch (transaction.getNumber()) {
                case 1:
                    circle.setImageResource(R.drawable.ic_schedule_pending_yellow_1);
                    break;
                case 2:
                    circle.setImageResource(R.drawable.ic_schedule_pending_yellow_2);
                    break;
                case 3:
                    circle.setImageResource(R.drawable.ic_schedule_pending_yellow_3);
                    break;
                case 4:
                    circle.setImageResource(R.drawable.ic_schedule_pending_yellow_4);
                    break;
            }
        } else {
            switch (transaction.getNumber()) {
                case 1:
                    circle.setImageResource(R.drawable.ic_schedule_pending_gray_1);
                    break;
                case 2:
                    circle.setImageResource(R.drawable.ic_schedule_pending_gray_2);
                    break;
                case 3:
                    circle.setImageResource(R.drawable.ic_schedule_pending_gray_3);
                    break;
                case 4:
                    circle.setImageResource(R.drawable.ic_schedule_pending_gray_4);
                    break;
            }
        }
    }
}
