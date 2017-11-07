package au.com.dealsdirect.ui.controller.orders.orders;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<GetPaymentsList.ResponseValue.PaymentItem> orderList = new ArrayList<>();
    Context context;

    public OrdersRecyclerViewAdapter(
            ArrayList<GetPaymentsList.ResponseValue.PaymentItem> orderList,
            Context context) {

        this.orderList = orderList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_container, parent, false);
        OrdersViewHolder holder = new OrdersViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {
        Log.d("myorders", " on bind view holder = " + position);

        OrdersViewHolder viewHolder = (OrdersViewHolder) vh;

        viewHolder.orderItemsRecyclerView.setAdapter(new OrdersItemRecyclerViewAdapter(
                orderList.get(position).getPaymentReferenceNo(),
                orderList.get(position).getOrders(),
                context));

        viewHolder.orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager
                .VERTICAL, false));

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        if (orderList == null) {
            return 0;
        }

        return orderList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void replace(ArrayList<GetPaymentsList.ResponseValue.PaymentItem> orders) {
        orderList = orders;
        notifyDataSetChanged();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_order_orders_recyclerview)
        RecyclerView orderItemsRecyclerView;


        public OrdersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
