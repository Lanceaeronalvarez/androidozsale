package au.com.dealsdirect.ui.controller.orders.orders;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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


    private ArrayList<GetPaymentsList.ResponseValue.Order> mOrderList = new ArrayList<>();
    private Context mContext;
    private int mPaymentReferenceNo;

    public OrdersItemRecyclerViewAdapter(
            int paymentReferenceNo,
            ArrayList<GetPaymentsList.ResponseValue.Order> orderList,
            Context context) {

        this.mOrderList = orderList;
        this.mContext = context;
        this.mPaymentReferenceNo = paymentReferenceNo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_orders, parent, false);
        OrderItemsViewHolder holder = new OrderItemsViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {

        GetPaymentsList.ResponseValue.Order item = mOrderList.get(position);
        OrderItemsViewHolder holder = (OrderItemsViewHolder) vh;

        String orderNumber = Integer.toString(mPaymentReferenceNo);
        String orderName = item.getDescription();
        String orderItemCount = item.getSubTotal().getItemsCount() + "";
        String orderStatus = item.getStatus();

        String trackerLink = item.getLink();
        if (trackerLink.isEmpty()) {
            holder.orderTrackHereContainer.setVisibility(View.GONE);
        }

        String orderItemsAmount = PriceUtils.getPriceStringValue(mOrderList.get(position).getSubTotal().getItemsAmount());

        holder.orderItemsAmount.setText(orderItemsAmount);
        holder.orderNumberValueTextView.setText(orderNumber);
        if (position != 0) {
            holder.orderNumberContainerLayout.setVisibility(View.GONE);
        } else {
            holder.orderNumberValueTextView.setText(orderNumber);
        }

        holder.orderProductNameTextView.setText(orderName);
        String itemText = " item";
        if (Integer.parseInt(orderItemCount) > 1) {
            itemText = " items";
        }
        holder.orderProductQuantityTextView.setText(orderItemCount + itemText);

        String approvedDate = DateUtils.getDateForOrderProgress(item.getTracker().getApprovedDate());
        String stockDate = DateUtils.getDateForOrderProgress(item.getTracker().getStockDate());
        String closeDate = DateUtils.getDateForOrderProgress(item.getTracker().getClosedDate());
        String dispatchDate = DateUtils.getDateForOrderProgress(item.getTracker().getDispatchedDate());

        holder.orderDateValueTextView.setText(approvedDate);
        holder.stockArrivedValueTextView.setText(stockDate);
        holder.dispatchedDateValueTextView.setText(closeDate);
        holder.orderPackedValueTextView.setText(dispatchDate);

        int currentStep = item.getTracker().getStep();
        int colorActive = holder.itemView.getResources().getColor(R.color.colorAccent);

        switch (currentStep) {
            case 1:
                holder.orderFirstNodeStatus.setText(orderStatus);
                holder.orderDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.orderDateGraphNodeTextView.setText("");

                holder.orderStockArrivedConnector.setBackgroundColor(colorActive);

                holder.stockArrivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_pending_state);
                holder.stockArrivedGraphNodeTextView.setTextColor(Color.WHITE);
                break;
            case -1:
                holder.orderFirstNodeStatus.setText(orderStatus);
                if (orderStatus.toLowerCase().contains(mContext.getString(R.string.refunded))) {
                    holder.orderDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_refunded_state);
                } else {
                    holder.orderDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_negative_state);
                }
                holder.orderDateGraphNodeTextView.setText("");
                break;
            case 2:
                holder.orderSecondNodeStatus.setText(orderStatus);
                holder.stockArrivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockArrivedGraphNodeTextView.setText("");

                holder.orderStockArrivedConnector.setBackgroundColor(colorActive);
                holder.orderPackedConnector.setBackgroundColor(colorActive);

                holder.orderPackedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_pending_state);
                holder.orderPackedGraphNodeTextView.setTextColor(Color.WHITE);
                break;
            case -2:
                holder.orderSecondNodeStatus.setText(orderStatus);

                if (orderStatus.toLowerCase().contains(mContext.getString(R.string.refunded))) {
                    holder.stockArrivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_refunded_state);
                } else {
                    holder.stockArrivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_negative_state);
                }
                holder.stockArrivedGraphNodeTextView.setText("");

                holder.orderStockArrivedConnector.setBackgroundColor(colorActive);

                break;

            case 3:
                holder.orderThirdNodeStatus.setText(orderStatus);

                holder.stockArrivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockArrivedGraphNodeTextView.setText("");

                holder.orderPackedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.orderPackedGraphNodeTextView.setText("");

                holder.orderStockArrivedConnector.setBackgroundColor(colorActive);
                holder.orderPackedConnector.setBackgroundColor(colorActive);
                holder.orderDispatchedConnector.setBackgroundColor(colorActive);

                holder.dispatchedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_pending_state);
                holder.dispatchedGraphNodeTextView.setTextColor(Color.WHITE);

                break;

            case -3:
                holder.orderThirdNodeStatus.setText(orderStatus);

                holder.stockArrivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockArrivedGraphNodeTextView.setText("");

                if (orderStatus.toLowerCase().contains(mContext.getString(R.string.refunded))) {
                    holder.orderPackedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_refunded_state);
                } else {
                    holder.orderPackedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_negative_state);
                }
                holder.orderPackedGraphNodeTextView.setText("");

                holder.orderStockArrivedConnector.setBackgroundColor(colorActive);
                holder.orderPackedConnector.setBackgroundColor(colorActive);

                break;

            case 4:
                holder.orderFourthNodeStatus.setText(orderStatus);

                holder.stockArrivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockArrivedGraphNodeTextView.setText("");
                holder.orderPackedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.orderPackedGraphNodeTextView.setText("");
                holder.dispatchedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.dispatchedGraphNodeTextView.setText("");

                holder.orderDispatchedConnector.setBackgroundColor(colorActive);
                holder.orderStockArrivedConnector.setBackgroundColor(colorActive);
                holder.orderPackedConnector.setBackgroundColor(colorActive);

                break;

            case -4:
                holder.orderFourthNodeStatus.setText(orderStatus);

                holder.stockArrivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockArrivedGraphNodeTextView.setText("");
                holder.orderPackedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.orderPackedGraphNodeTextView.setText("");

                if (orderStatus.toLowerCase().contains(mContext.getString(R.string.refunded))) {
                    holder.dispatchedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_refunded_state);
                } else {
                    holder.dispatchedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_negative_state);
                }

                holder.dispatchedGraphNodeTextView.setText("");

                holder.orderDispatchedConnector.setBackgroundColor(colorActive);
                holder.orderStockArrivedConnector.setBackgroundColor(colorActive);
                holder.orderPackedConnector.setBackgroundColor(colorActive);
                break;


            default:
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        if (mOrderList == null) {
            return 0;
        }

        return mOrderList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void replace(ArrayList<GetPaymentsList.ResponseValue.Order> orders) {
        mOrderList = orders;
        notifyDataSetChanged();
    }

    public class OrderItemsViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumberValueTextView;
        TextView orderProductNameTextView;
        TextView orderProductQuantityTextView;

        TextView orderDateGraphNodeTextView;
        TextView orderDateValueTextView;

        TextView stockArrivedGraphNodeTextView;
        TextView stockArrivedValueTextView;

        TextView orderPackedGraphNodeTextView;
        TextView orderPackedValueTextView;

        TextView dispatchedDateValueTextView;
        TextView dispatchedGraphNodeTextView;

        TextView orderStatusTextView;
        TextView orderDateTextView;
        TextView orderEstimatedDeliveryDateTextView;
        TextView orderItemsAmount;

        TextView orderFirstNodeStatus;
        TextView orderSecondNodeStatus;
        TextView orderThirdNodeStatus;
        TextView orderFourthNodeStatus;

        View orderStockArrivedConnector;
        View orderPackedConnector;
        View orderDispatchedConnector;

        Button trackHereButton;
        LinearLayout orderTrackHereContainer;

        RelativeLayout orderNumberContainerLayout;

        public OrderItemsViewHolder(View itemView) {
            super(itemView);

            orderNumberContainerLayout = (RelativeLayout) itemView.findViewById(R.id.my_order_number_container);
            orderTrackHereContainer = (LinearLayout) itemView.findViewById(R.id.row_item_order_track_her_container);

            orderStockArrivedConnector = itemView.findViewById(R.id.connector_to_stock_arrived);
            orderPackedConnector = itemView.findViewById(R.id.connector_to_order_packed);
            orderDispatchedConnector = itemView.findViewById(R.id.connector_to_dispatched);

            orderNumberValueTextView = (TextView) itemView.findViewById(R.id.order_number_text_value);
            orderProductNameTextView = (TextView) itemView.findViewById(R.id.my_order_product_name);
            orderProductQuantityTextView = (TextView) itemView.findViewById(R.id.productQuantityTextView);

            orderDateGraphNodeTextView = (TextView) itemView.findViewById(R.id.order_date_graph_node);
            orderDateValueTextView = (TextView) itemView.findViewById(R.id.order_date_value);

            stockArrivedGraphNodeTextView = (TextView) itemView.findViewById(R.id.stock_arrived_graph_node);
            stockArrivedValueTextView = (TextView) itemView.findViewById(R.id.stock_arrived_value);

            orderPackedGraphNodeTextView = (TextView) itemView.findViewById(R.id.order_packed_graph_node);
            orderPackedValueTextView = (TextView) itemView.findViewById(R.id.order_packed_value);

            dispatchedGraphNodeTextView = (TextView) itemView.findViewById(R.id.dispatched_graph_node);
            dispatchedDateValueTextView = (TextView) itemView.findViewById(R.id.dispatched_date_value);

            orderStatusTextView = (TextView) itemView.findViewById(R.id.status_text_view);
            orderDateTextView = (TextView) itemView.findViewById(R.id.order_date_text_view);
            orderEstimatedDeliveryDateTextView = (TextView) itemView.findViewById(R.id.estimated_delivery_date_text_view);
            orderItemsAmount = (TextView) itemView.findViewById(R.id.my_order_product_items_amount);

            orderFirstNodeStatus = (TextView) itemView.findViewById(R.id.tracker_first_node);
            orderSecondNodeStatus = (TextView) itemView.findViewById(R.id.tracker_second_node);
            orderThirdNodeStatus = (TextView) itemView.findViewById(R.id.tracker_third_node);
            orderFourthNodeStatus = (TextView) itemView.findViewById(R.id.tracker_fourth_node);

            trackHereButton = (Button) itemView.findViewById(R.id.trackHereButton);
        }
    }
}