package au.com.dealsdirect.ui.controller.dashboard.details;
/*
 * Created by CodeineBot on 6/7/17.
 */

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
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
import au.com.dealsdirect.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentDetailsAdapter extends RecyclerView.Adapter<PaymentDetailsAdapter.ViewHolder> {

    Context mContext;
    PaymentPlan mPaymentPlan;
    List<PlannedTransaction> mData;
    String mLastMonth = "";

    public PaymentDetailsAdapter(PaymentPlan paymentPlan, List<PlannedTransaction> data) {
        this.mPaymentPlan = paymentPlan;
        this.mData = data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_payment_schedule_day)
        TextView day;

        @BindView(R.id.row_payment_schedule_month)
        TextView month;

        @BindView(R.id.row_payment_schedule_card_image)
        ImageView cardImage;

        @BindView(R.id.row_payment_schedule_card_number)
        TextView cardNumber;

        @BindView(R.id.row_payment_schedule_value)
        TextView value;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_payment_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final PlannedTransaction item = mData.get(position);

        holder.day.setText(DateUtils.getDateFromStringInFormat(item.getPlannedDate(), "dd"));
        holder.month.setText(DateUtils.getDateFromStringInFormat(item.getPlannedDate(), "MMM"));
        holder.cardNumber.setText(mPaymentPlan.getOrderNo());
        holder.value.setText(String.format(Locale.getDefault(), "%s%.2f", mPaymentPlan.getCurrencySign(), item.getAmount()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
