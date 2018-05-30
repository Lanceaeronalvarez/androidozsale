package au.com.dealsdirect.ui.controller.orders.orders;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<OrdersRecyclerViewAdapter.OrdersViewHolder> {

    public ArrayList<GetPaymentsList.ResponseValue.PaymentItem> mOrderList = new ArrayList<>();
    Context mContext;

    public OrdersRecyclerViewAdapter(Context context,
                                     ArrayList<GetPaymentsList.ResponseValue.PaymentItem> orderList) {

        this.mOrderList = orderList;
        this.mContext = context;
    }

    @Override
    public OrdersRecyclerViewAdapter.OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_container, parent, false);

        return new OrdersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrdersRecyclerViewAdapter.OrdersViewHolder viewHolder, final int position) {
        Log.d("myorders", " on bind view holder = " + position);

        viewHolder.orderItemsRecyclerView.setAdapter(new OrdersItemRecyclerViewAdapter(
                mOrderList.get(position).getPaymentReferenceNo(),
                mOrderList.get(position).getOrders(),
                mContext));

        GetPaymentsList.ResponseValue.Total total = mOrderList.get(position).getTotal();

        viewHolder.cardPaymentText.setText(String.format(mContext.getString(R.string.dollar), String.valueOf(total.getCreditCardAmount())));
        viewHolder.voucherPaymentText.setText(String.format(mContext.getString(R.string.dollar), String.valueOf(total.getDiscountAmount())));
        viewHolder.totalPaymentText.setText(String.format(mContext.getString(R.string.dollar), String.valueOf(total.getTotalAmount())));

        viewHolder.orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager
                .VERTICAL, false));

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void replace(ArrayList<GetPaymentsList.ResponseValue.PaymentItem> orders) {
        mOrderList = orders;
        notifyDataSetChanged();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_order_orders_recyclerview)
        RecyclerView orderItemsRecyclerView;

        @BindView(R.id.row_order_card_payment_value)
        TextView cardPaymentText;

        @BindView(R.id.row_order_voucher_payment_value)
        TextView voucherPaymentText;

        @BindView(R.id.row_order_total_value)
        TextView totalPaymentText;


        public OrdersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
