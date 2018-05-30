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
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersItemRecyclerViewAdapter extends RecyclerView.Adapter<OrdersItemRecyclerViewAdapter.OrderItemsViewholder> {

    private ArrayList<GetPaymentsList.ResponseValue.Order> mOrderList = new ArrayList<>();
    private Context mContext;
    private int mPaymentReferenceNo;

    private static final int ORDER_DATE_ACTIVE_STATE = 1;
    private static final int ORDER_DATE_NEGATIVE_STATE = -1;
    private static final int ORDER_STOCK_ARRIVED_ACTIVE_STATE = 2;
    private static final int ORDER_STOCK_ARRIVED_NEGATIVE_STATE = -2;
    private static final int ORDER_PACKED_ACTIVE_STATE = 3;
    private static final int ORDER_PACKED_NEGATIVE_STATE = -3;
    private static final int ORDER_DISPATCHED_ACTIVE_STATE = 4;
    private static final int ORDER_DISPATCHED_NEGATIVE_STATE = -4;


    public OrdersItemRecyclerViewAdapter(
            int paymentReferenceNo,
            ArrayList<GetPaymentsList.ResponseValue.Order> orderList,
            Context context) {

        this.mOrderList = orderList;
        this.mContext = context;
        this.mPaymentReferenceNo = paymentReferenceNo;
    }


    @Override
    public void onBindViewHolder(OrderItemsViewholder holder, int position) {

        GetPaymentsList.ResponseValue.Order item = mOrderList.get(position);

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
        String itemText = Integer.parseInt(orderItemCount) > 1 ? " item" : " items";

        holder.estimatedDeliveryText.setText(mOrderList.get(position).getEstimatedDeliveryText());
        holder.itemInvoiceNumberText.setText(String.valueOf(mOrderList.get(position).getInvoiceNo()));

        holder.orderProductQuantityTextView.setText(orderItemCount + itemText);

        String approvedDate = DateUtils.getDateForOrderProgress(item.getTracker().getApprovedDate());
        String stockDate = DateUtils.getDateForOrderProgress(item.getTracker().getStockDate());
        String closeDate = DateUtils.getDateForOrderProgress(item.getTracker().getClosedDate());
        String dispatchDate = DateUtils.getDateForOrderProgress(item.getTracker().getDispatchedDate());

        int currentStep = item.getTracker().getStep();
        int colorActive = holder.itemView.getResources().getColor(R.color.colorAccent);
        boolean isRefunded = orderStatus.toLowerCase().contains(mContext.getString(R.string.refunded));
        switch (currentStep) {
            case ORDER_DATE_ACTIVE_STATE:
                holder.orderDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                setupOrderDateNode(holder, orderStatus);

                holder.orderStockArrivedConnector.setBackgroundColor(colorActive);
                holder.stockArrivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_pending_state);
                holder.stockArrivedGraphNodeTextView.setTextColor(Color.WHITE);
                break;

            case ORDER_DATE_NEGATIVE_STATE:
                holder.orderFirstNodeStatus.setTextColor(mContext.getResources().getColor(isRefunded ? R.color.ourpay_red : R.color.text_medium));
                holder.orderDateGraphNodeTextView.setBackgroundResource(isRefunded ? R.drawable.bg_orders_refunded_state : R.drawable.bg_orders_negative_state);
                if(isRefunded) {
                    approvedDate += " Refunded";
                }
                setupOrderDateNode(holder, orderStatus);
                break;

            case ORDER_STOCK_ARRIVED_ACTIVE_STATE:
                holder.stockArrivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                setupOrderStockArrived(holder, orderStatus, colorActive);

                holder.orderPackedConnector.setBackgroundColor(colorActive);
                holder.orderPackedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_pending_state);
                holder.orderPackedGraphNodeTextView.setTextColor(Color.WHITE);
                break;

            case ORDER_STOCK_ARRIVED_NEGATIVE_STATE:
                holder.orderSecondNodeStatus.setTextColor(mContext.getResources().getColor(isRefunded ? R.color.ourpay_red : R.color.text_medium));
                holder.stockArrivedGraphNodeTextView.setBackgroundResource(isRefunded ? R.drawable.bg_orders_refunded_state : R.drawable.bg_orders_negative_state);
                if(isRefunded) {
                    stockDate += " Refunded";
                }
                setupOrderStockArrived(holder, orderStatus, colorActive);
                break;

            case ORDER_PACKED_ACTIVE_STATE:
                holder.orderPackedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                setupOrderPackedNode(holder, orderStatus, colorActive);

                holder.orderDispatchedConnector.setBackgroundColor(colorActive);
                holder.dispatchedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_pending_state);
                holder.dispatchedGraphNodeTextView.setTextColor(Color.WHITE);
                break;

            case ORDER_PACKED_NEGATIVE_STATE:
                holder.orderThirdNodeStatus.setTextColor(mContext.getResources().getColor(isRefunded ? R.color.ourpay_red : R.color.text_medium));
                holder.orderPackedGraphNodeTextView.setBackgroundResource(isRefunded ? R.drawable.bg_orders_refunded_state : R.drawable.bg_orders_negative_state);
                if(isRefunded) {
                    dispatchDate += " Refunded";
                }
                setupOrderPackedNode(holder, orderStatus, colorActive);
                break;

            case ORDER_DISPATCHED_ACTIVE_STATE:
                holder.dispatchedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                setupDispatchNode(holder, orderStatus, colorActive);
                break;

            case ORDER_DISPATCHED_NEGATIVE_STATE:
                holder.orderFourthNodeStatus.setTextColor(mContext.getResources().getColor(isRefunded ? R.color.ourpay_red : R.color.text_medium));
                holder.dispatchedGraphNodeTextView.setBackgroundResource(isRefunded ? R.drawable.bg_orders_refunded_state : R.drawable.bg_orders_negative_state);
                if(isRefunded) {
                     closeDate += " Refunded";
                }
                setupDispatchNode(holder, orderStatus, colorActive);
                break;
        }


        holder.orderDateValueTextView.setText(approvedDate);
        holder.stockArrivedValueTextView.setText(stockDate);
        holder.dispatchedDateValueTextView.setText(closeDate);
        holder.orderPackedValueTextView.setText(dispatchDate);
    }

    private void setupOrderDateNode(OrderItemsViewholder holder, String orderStatus) {
        holder.orderDateGraphNodeTextView.setText("");
    }

    private void setupOrderStockArrived(OrderItemsViewholder holder, String orderStatus, int colorActive) {
        holder.stockArrivedGraphNodeTextView.setText("");

        holder.orderStockArrivedConnector.setBackgroundColor(colorActive);
    }

    private void setupOrderPackedNode(OrderItemsViewholder holder, String orderStatus, int colorActive) {

        holder.stockArrivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
        holder.stockArrivedGraphNodeTextView.setText("");

        holder.orderStockArrivedConnector.setBackgroundColor(colorActive);
        holder.orderPackedConnector.setBackgroundColor(colorActive);
    }

    private void setupDispatchNode(OrderItemsViewholder holder, String orderStatus, int colorActive) {
        holder.dispatchedGraphNodeTextView.setText("");

        holder.orderDispatchedConnector.setBackgroundColor(colorActive);
        holder.orderStockArrivedConnector.setBackgroundColor(colorActive);
        holder.orderPackedConnector.setBackgroundColor(colorActive);
    }

    @Override
    public OrderItemsViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_orders, parent, false);

        return new OrderItemsViewholder(v);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mOrderList == null ? 0 : mOrderList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void replace(ArrayList<GetPaymentsList.ResponseValue.Order> orders) {
        mOrderList = orders;
        notifyDataSetChanged();
    }

    public class OrderItemsViewholder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_number_text_value)
        TextView orderNumberValueTextView;
        @BindView(R.id.controller_order_details_item_product_name_textview)
        TextView orderProductNameTextView;
        @BindView(R.id.controller_order_details_item_quantity_textview)
        TextView orderProductQuantityTextView;

        @BindView(R.id.order_date_graph_node)
        TextView orderDateGraphNodeTextView;
        @BindView(R.id.order_date_value)
        TextView orderDateValueTextView;

        @BindView(R.id.stock_arrived_graph_node)
        TextView stockArrivedGraphNodeTextView;
        @BindView(R.id.stock_arrived_value)
        TextView stockArrivedValueTextView;

        @BindView(R.id.order_packed_graph_node)
        TextView orderPackedGraphNodeTextView;
        @BindView(R.id.order_packed_value)
        TextView orderPackedValueTextView;

        @BindView(R.id.dispatched_graph_node)
        TextView dispatchedDateValueTextView;
        @BindView(R.id.dispatched_date_value)
        TextView dispatchedGraphNodeTextView;

        @BindView(R.id.controller_order_amount_text)
        TextView orderItemsAmount;

        @BindView(R.id.tracker_first_node)
        TextView orderFirstNodeStatus;
        @BindView(R.id.tracker_second_node)
        TextView orderSecondNodeStatus;
        @BindView(R.id.tracker_third_node)
        TextView orderThirdNodeStatus;
        @BindView(R.id.tracker_fourth_node)
        TextView orderFourthNodeStatus;

        @BindView(R.id.connector_to_stock_arrived)
        View orderStockArrivedConnector;
        @BindView(R.id.connector_to_order_packed)
        View orderPackedConnector;
        @BindView(R.id.connector_to_dispatched)
        View orderDispatchedConnector;

        @BindView(R.id.trackHereButton)
        Button trackHereButton;

        @BindView(R.id.row_item_order_track_her_container)
        LinearLayout orderTrackHereContainer;

        @BindView(R.id.my_order_number_container)
        RelativeLayout orderNumberContainerLayout;

        @BindView(R.id.estimatedDeliveryTextView)
        TextView estimatedDeliveryText;

        @BindView(R.id.itemInvoiceNumberTextView)
        TextView itemInvoiceNumberText;

        public OrderItemsViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}