package au.com.dealsdirect.ui.controller.dashboard.pastpayments;
/*
 * Created by CodeineBot on 6/7/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;
import java.util.Locale;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.PastPayment;
import au.com.dealsdirect.ui.controller.dashboard.DashboardController;
import au.com.dealsdirect.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PastPaymentsAdapter extends RecyclerView.Adapter<PastPaymentsAdapter.ViewHolder> {

    Context mContext;
    List<PastPayment> mData;
    String mLastMonth = "";

    public PastPaymentsAdapter(List<PastPayment> data) {
        this.mData = data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vh_payment_month_header)
        TextView header;

        @BindView(R.id.viewholder_payment_day)
        TextView day;

        @BindView(R.id.viewholder_payment_month)
        TextView month;

        @BindView(R.id.viewholder_payment_title)
        TextView title;

        @BindView(R.id.viewholder_payment_id)
        TextView id;

        @BindView(R.id.viewholder_payment_card_number)
        TextView card;

        @BindView(R.id.viewholder_payment_value)
        TextView value;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_payment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final PastPayment item = mData.get(position);

        if (mLastMonth.equals(DateUtils.getDateFromStringInFormat(item.getPlannedDate(), "MMMM yyyy"))) {
            holder.header.setVisibility(View.GONE);
        }
        mLastMonth = DateUtils.getDateFromStringInFormat(item.getPlannedDate(), "MMMM yyyy");

        holder.header.setText(DateUtils.getDateFromStringInFormat(item.getPlannedDate(), "MMMM yyyy"));
        holder.day.setText(DateUtils.getDateFromStringInFormat(item.getPlannedDate(), "dd"));
        holder.month.setText(DateUtils.getDateFromStringInFormat(item.getPlannedDate(), "MMM"));
        holder.title.setText(item.getName());
        holder.id.setText(item.getOrderNo());
        holder.card.setText(item.getMaskedNumber());
        holder.value.setText(String.format(Locale.getDefault(), "%s%.2f", item.getCurrency(), item.getAmount()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
