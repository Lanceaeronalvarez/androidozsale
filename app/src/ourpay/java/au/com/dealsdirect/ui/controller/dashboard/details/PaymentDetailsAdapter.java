package au.com.dealsdirect.ui.controller.dashboard.details;
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

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.ourpaydashboard.Payment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentDetailsAdapter extends RecyclerView.Adapter<PaymentDetailsAdapter.ViewHolder> {

    Context mContext;
    PaymentDetailsController mController;
    List<Payment> mData;
    String mLastMonth = "";

    public PaymentDetailsAdapter(PaymentDetailsController controller, List<Payment> data) {
        this.mController = controller;
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

        final Payment item = mData.get(position);

        holder.day.setText(item.getDay());
        holder.month.setText(item.getMonth().substring(0, 3));
        holder.cardNumber.setText(item.getCardNumber());
        holder.value.setText(item.getCurrency() + item.getValue());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
