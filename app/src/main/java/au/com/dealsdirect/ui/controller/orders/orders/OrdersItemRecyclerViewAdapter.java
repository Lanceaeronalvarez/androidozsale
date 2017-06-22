package au.com.dealsdirect.ui.controller.orders.orders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.PriceUtils;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

//    private final MyOrderClickListener mListener;


    public ArrayList<GetPaymentsList.ResponseValue.Order> orderList = new ArrayList<>();
    Context context;
    int paymentReferenceNo;

    public OrdersItemRecyclerViewAdapter(
            int paymentReferenceNo,
            ArrayList<GetPaymentsList.ResponseValue.Order> orderList,
            Context context) {
        Log.d("myorders", "size inside when set adapter = " + orderList.size());

        this.orderList = orderList;
        this.context = context;
        this.paymentReferenceNo = paymentReferenceNo;
//        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_orders, parent,
                        false);
        OrderItemsViewHolder holder = new OrderItemsViewHolder(v);
        //mView = v;

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {

        Log.d("myorders", " on bind view holder = " + position + " ,  payment reference no. = " + paymentReferenceNo
                + " items amount = " + orderList.get(position).getSubTotal().getItemsAmount());

        GetPaymentsList.ResponseValue.Order item = orderList.get(position);
        OrderItemsViewHolder holder = (OrderItemsViewHolder )vh;

        String orderNumber = Integer.toString(paymentReferenceNo);
        String orderName = item.getDescription();
        String orderItemCount = item.getSubTotal().getItemsCount() + "";
        String orderStatus = item.getStatus();

        String trackerLink = item.getLink();
        if (trackerLink.isEmpty()) {
            holder.trackHereButton.setVisibility(View.GONE);
        }

        String orderItemsAmount = PriceUtils.getPriceStringValue(orderList.get(position).getSubTotal().getItemsAmount());

        holder.orderItemsAmount.setText(orderItemsAmount);
        holder.orderNumberValueTextView.setText(orderNumber);
        if (position != 0) {
            holder.orderNumberContainerLayout.setVisibility(View.INVISIBLE);
        } else {
            holder.orderNumberValueTextView.setText(orderNumber);
        }

        holder.orderProductNameTextView.setText(orderName);
        String itemText = " item";
        if (Integer.parseInt(orderItemCount) > 1) {
            itemText = " items";
        }
        holder.orderProductQuantityTextView.setText(orderItemCount + itemText);

        String approvedDate = DateUtils.getDateForOrderProgress(item.getTracker().getApprovedDate
                ());
        String dispatchDate = DateUtils.getDateForOrderProgress(item.getTracker()
                .getDispatchedDate());
        String closeDate = DateUtils.getDateForOrderProgress(item.getTracker().getClosedDate());
        String stockDate = DateUtils.getDateForOrderProgress(item.getTracker().getStockDate());


        holder.approvedDateValueTextView.setText(approvedDate);
        holder.stockDateValueTextView.setText(stockDate);
        holder.dispatchedDateValueTextView.setText(closeDate);
        holder.closedDateValueTextView.setText(dispatchDate);
        int currentStep = item.getTracker().getStep();


        switch (currentStep) {
            case 1:
                holder.orderFirstNodeStatus.setText(orderStatus);
                holder.approvedDateGraphNodeImageView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.approvedDateGraphNodeImageView.setText("");
                break;

            case -1:
                holder.orderFirstNodeStatus.setText(orderStatus);
                holder.approvedDateGraphNodeImageView.setBackgroundResource(R.drawable.bg_orders_negative_state);
                holder.approvedDateGraphNodeImageView.setText("");
                break;
            case 2:
                holder.stockDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockDateGraphNodeTextView.setText("");
//                holder.stockDateValueTextView.setVisibility(View.INVISIBLE);
                holder.orderSecondNodeStatus.setText("Stock Arrived");
                break;
            case -2:
                holder.stockDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_negative_state);
                holder.stockDateGraphNodeTextView.setText("");
//                holder.stockDateValueTextView.setVisibility(View.INVISIBLE);
                holder.orderSecondNodeStatus.setText(orderStatus);
                break;

            case 3:
                holder.stockDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockDateGraphNodeTextView.setText("");
                holder.dispatchedDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.dispatchedDateGraphNodeTextView.setText("");
//                holder.dispatchedDateValueTextView.setVisibility(View.INVISIBLE);
                holder.orderThirdNodeStatus.setText("Order Packed");
                break;

            case -3:
                holder.stockDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockDateGraphNodeTextView.setText("");
                holder.dispatchedDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_negative_state);
                holder.dispatchedDateGraphNodeTextView.setText("");
//                holder.dispatchedDateValueTextView.setVisibility(View.INVISIBLE);
                holder.orderThirdNodeStatus.setText(orderStatus);
                break;

            case 4:
                holder.stockDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockDateGraphNodeTextView.setText("");
                holder.dispatchedDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.dispatchedDateGraphNodeTextView.setText("");
                holder.closedDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.closedDateGraphNodeTextView.setText("");
//                holder.closedDateValueTextView.setVisibility(View.INVISIBLE);
                holder.orderFourthNodeStatus.setText("Dispatched");
                break;

            case -4:
                holder.stockDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockDateGraphNodeTextView.setText("");
                holder.dispatchedDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.dispatchedDateGraphNodeTextView.setText("");
                holder.closedDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_negative_state);
                holder.closedDateGraphNodeTextView.setText("");
//                holder.closedDateValueTextView.setVisibility(View.INVISIBLE);
//                holder.orderFourthNodeStatus.setText(orderStatus);
                break;


            default:
                break;
        }

//        holder.orderDateTextView.setText(DateUtils.getTrimmedServerDateString(item.getTracker().getApprovedDate()));
//        String dateString = item.getEstimatedDeliveryText();
//        holder.orderEstimatedDeliveryDateTextView.setText(dateString.substring(dateString.indexOf(":")+1));

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

        Log.d("myorders", "size in items recyclerview adapter = " + orderList.size());
        return orderList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void replace(ArrayList<GetPaymentsList.ResponseValue.Order> orders) {
        orderList = orders;
        notifyDataSetChanged();
    }

    public class OrderItemsViewHolder extends RecyclerView.ViewHolder {

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
        TextView orderItemsAmount;

        TextView orderFirstNodeStatus;
        TextView orderSecondNodeStatus;
        TextView orderThirdNodeStatus;
        TextView orderFourthNodeStatus;

        Button trackHereButton;

        RelativeLayout orderNumberContainerLayout;

        public OrderItemsViewHolder(View itemView) {
            super(itemView);

            orderNumberContainerLayout = (RelativeLayout) itemView.findViewById(R.id.my_order_number_container);

            orderNumberValueTextView = (TextView) itemView.findViewById(R.id.order_number_text_value);
            orderProductNameTextView = (TextView) itemView.findViewById(R.id.my_order_product_name);
            orderProductQuantityTextView = (TextView) itemView.findViewById(R.id.productQuantityTextView);

            approvedDateGraphNodeImageView = (TextView) itemView.findViewById(R.id
                    .approved_date_graph_node);
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
            orderItemsAmount = (TextView) itemView.findViewById(R.id.my_order_product_items_amount);

            orderFirstNodeStatus = (TextView) itemView.findViewById(R.id.tracker_first_node);
            orderSecondNodeStatus = (TextView) itemView.findViewById(R.id.tracker_second_node);
            orderThirdNodeStatus = (TextView) itemView.findViewById(R.id.tracker_third_node);
            orderFourthNodeStatus = (TextView) itemView.findViewById(R.id.tracker_fourth_node);

            trackHereButton = (Button) itemView.findViewById(R.id.trackHereButton);
        }
    }
}