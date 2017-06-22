package au.com.dealsdirect.ui.controller.orders.orders;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_orders, parent, false);
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

        TextView orderNumberValueTextView;
        TextView orderProductNameTextView;
        TextView orderProductQuantityTextView;

        TextView approvedDateGraphNodeImageView;
        TextView approvedDateValueTextView;

        TextView stockDateGraphNodeTextView;
        TextView stockDateValueTextView;

        TextView dispatchedDateGraphNodeTextView;
        TextView dispatchedDateValueTextView;

        TextView closedDateGraphNodeTextView;
        TextView closedDateValueTextView;

        TextView orderStatusTextView;
        TextView orderDateTextView;
        TextView orderEstimatedDeliveryDateTextView;

        RecyclerView orderItemsRecyclerView;


        public OrdersViewHolder(View itemView) {
            super(itemView);

            orderItemsRecyclerView = (RecyclerView) itemView.findViewById(R.id.order_items_recyclerview);

            orderNumberValueTextView = (TextView) itemView.findViewById(R.id.order_number_text_value);
            orderProductNameTextView = (TextView) itemView.findViewById(R.id.my_order_product_name);
            orderProductQuantityTextView = (TextView) itemView.findViewById(R.id.productQuantityTextView);

            approvedDateGraphNodeImageView = (TextView) itemView.findViewById(R.id.approved_date_graph_node);
            approvedDateValueTextView = (TextView) itemView.findViewById(R.id.approved_date_value);

            stockDateGraphNodeTextView = (TextView) itemView.findViewById(R.id.stock_date_graph_node);
            stockDateValueTextView = (TextView) itemView.findViewById(R.id.stock_date_value);

            dispatchedDateGraphNodeTextView = (TextView) itemView.findViewById(R.id.dispatched_date_graph_node);
            dispatchedDateValueTextView = (TextView) itemView.findViewById(R.id.dispatched_date_value);

            closedDateGraphNodeTextView = (TextView) itemView.findViewById(R.id.closed_date_graph_node);
            closedDateValueTextView = (TextView) itemView.findViewById(R.id.closed_date_value);

            orderStatusTextView = (TextView) itemView.findViewById(R.id.statusTextView);
            orderDateTextView = (TextView) itemView.findViewById(R.id.orderDateTextView);
            orderEstimatedDeliveryDateTextView = (TextView) itemView.findViewById(R.id.estimatedDeliveryDateTextView);

        }
    }
}
