package au.com.dealsdirect.ui.controller.orders.orders;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.utils.ActionConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<GetPaymentsList.ResponseValue.PaymentItem> mPaymentItemList;
    private Activity mActivity;
    private OrderItemClickListener mClickListener;

    public static final int TITLE_VIEW_TYPE = 10;
    public static final int DETAILS_VIEW_TYPE = 11;

    private ArrayList<Object> mData;
    private ArrayList<String> mReferenceNumbers;
    private GetPaymentsList.ResponseValue.PaymentItem item;
    private LinkedHashMap<String, Object> mLinkedHashMap;
    private HashMap<String, String> estDeliveryDate = new HashMap<>();
    private HashMap<String, String> mStatusArray = new HashMap<>();

    private static final int ORDER_DATE_ACTIVE_STATE = 1;
    private static final int ORDER_DATE_NEGATIVE_STATE = -1;
    private static final int ORDER_STOCK_ARRIVED_ACTIVE_STATE = 2;
    private static final int ORDER_STOCK_ARRIVED_NEGATIVE_STATE = -2;
    private static final int ORDER_PACKED_ACTIVE_STATE = 3;
    private static final int ORDER_PACKED_NEGATIVE_STATE = -3;
    private static final int ORDER_DISPATCHED_ACTIVE_STATE = 4;
    private static final int ORDER_DISPATCHED_NEGATIVE_STATE = -4;
    private static final int ORDER_RECEIVED_ACTIVE_STATE = 5;

    public OrdersRecyclerViewAdapter(Activity mActivity,
                                     OrderItemClickListener clickListener,
                                     ArrayList<GetPaymentsList.ResponseValue.PaymentItem> paymentItems) {

        this.mPaymentItemList = paymentItems;
        this.mClickListener = clickListener;
        this.mActivity = mActivity;
        flattenData();
        mReferenceNumbers = flattenReferenceNumber(mData);
    }

    @Override
    public int getItemViewType(int position) {
        //TODO: logic for determining what viewholder should be shown
        return mData.get(position) instanceof GetPaymentsList.ResponseValue.PaymentItem ? TITLE_VIEW_TYPE : DETAILS_VIEW_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TITLE_VIEW_TYPE) {
            return new OrdersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_container, parent, false));
        }
        return new OrderItemsViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_orders, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        int viewType = getItemViewType(position);

        if (viewType == TITLE_VIEW_TYPE) {
            AppLogger.d("Orders Title Item Position: " + position);
            item = (GetPaymentsList.ResponseValue.PaymentItem) mData.get(position);
            ((OrdersViewHolder) viewHolder).orderNumberTextView.setText(String.valueOf(item.getPaymentReferenceNo()));
        } else if (viewType == DETAILS_VIEW_TYPE) {
            GetPaymentsList.ResponseValue.Order order = (GetPaymentsList.ResponseValue.Order) mData.get(position);
            setOrderTrackData((OrderItemsViewholder) viewHolder, item, order);
            ((OrderItemsViewholder) viewHolder).trackHereButton.setOnClickListener(v ->
                    mClickListener.onOrderItemTrackingButtonClick(order.getLink(), null));

            if (order.getActions().size() == 0) {
                ((OrderItemsViewholder) viewHolder).orderOptions.setVisibility(View.GONE);
            } else {

                if (order.getActions().contains(ActionConstants.ORDER_ACTION_CHECK_STATUS) ||
                    order.getActions().contains(ActionConstants.ORDER_ACTION_CHANGE_ADDRESS) ||
                    order.getActions().contains(ActionConstants.ORDER_ACTION_REFUND)) {

                    ((OrderItemsViewholder) viewHolder).orderOptions.setVisibility(View.VISIBLE);
                }

                HashMap<String, String> itemArrays = new HashMap<>();
                itemArrays.put(ActionConstants.ORDER_ORDER_ID, order.getOrderID());
                itemArrays.put(ActionConstants.ORDER_ITEM_DESCRIPTION, order.getDescription());
                itemArrays.put(ActionConstants.ORDER_INVOICE_NUMBER, order.getInvoiceNo().toString());

                ((OrderItemsViewholder) viewHolder).orderOptions.setOnClickListener(v ->
                        mClickListener.onOrderItemShowOptions((ArrayList<String>) order.getActions(), itemArrays));
            }

            estDeliveryDate.put(order.getOrderID(), order.getEstimatedDeliveryText());
            mStatusArray.put(order.getOrderID(), order.getStatus());

            viewHolder.itemView.setOnClickListener(view -> mClickListener.onOrderItemClick(mReferenceNumbers.get(position),
                    mStatusArray, order.getLink(), estDeliveryDate));
        }

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private ArrayList<String> flattenReferenceNumber(ArrayList<Object> data) {
        ArrayList<String> referenceNumbers = new ArrayList<>();
        GetPaymentsList.ResponseValue.PaymentItem item = new GetPaymentsList.ResponseValue.PaymentItem();

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) instanceof GetPaymentsList.ResponseValue.PaymentItem) {
                item = (GetPaymentsList.ResponseValue.PaymentItem) data.get(i);
            }
            referenceNumbers.add(String.valueOf(item.getPaymentReferenceNo()));
        }
        return referenceNumbers;
    }

    private void flattenData() {
        mData = new ArrayList<>();

        for (GetPaymentsList.ResponseValue.PaymentItem item : mPaymentItemList) {
            mData.add(item);
            for (int i = 0; i < item.getOrders().size(); i++) {
                mData.add(item.getOrders().get(i));
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void replace(ArrayList<GetPaymentsList.ResponseValue.PaymentItem> orders) {
        mPaymentItemList = orders;
        notifyDataSetChanged();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_number_text_value)
        TextView orderNumberTextView;

        @BindView(R.id.my_order_number_container)
        RelativeLayout orderNumberContainerLayout;

        public OrdersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class OrderItemsViewholder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_image_recyclerview)
        RecyclerView orderImagesRecyclerView;

        @BindView(R.id.trackHereButton)
        Button trackHereButton;

        @BindView(R.id.order_date_graph_node)
        TextView orderDateGraphNodeView;
        @BindView(R.id.order_date_value)
        TextView orderDateValueTextView;

        @BindView(R.id.stock_arrived_graph_node)
        TextView stockArrivedGraphNodeView;
        @BindView(R.id.stock_arrived_value)
        TextView stockArrivedValueTextView;

        @BindView(R.id.order_packed_graph_node)
        TextView orderPackedGraphNodeView;
        @BindView(R.id.order_packed_value)
        TextView orderPackedValueTextView;

        @BindView(R.id.dispatched_graph_node)
        TextView dispatchedGraphNodeTextView;
        @BindView(R.id.dispatched_date_value)
        TextView dispatchedDateValueTextView;

        @BindView(R.id.received_graph_node)
        TextView receivedGraphNodeTextView;
        @BindView(R.id.received_date_value)
        TextView receivedDateValueTextView;

        @BindView(R.id.tracker_first_node)
        TextView orderFirstNodeStatusTextView;
        @BindView(R.id.tracker_second_node)
        TextView orderSecondNodeStatusTextView;
        @BindView(R.id.tracker_third_node)
        TextView orderThirdNodeStatusTextView;
        @BindView(R.id.tracker_fourth_node)
        TextView orderFourthNodeStatusTextView;
        @BindView(R.id.tracker_fifth_node)
        TextView orderFifthNodeStatusTextView;

        @BindView(R.id.connector_to_stock_arrived)
        View orderStockArrivedConnector;
        @BindView(R.id.connector_to_stock_arrived_2)
        View orderStockArrivedConnector2;
        @BindView(R.id.connector_to_order_packed)
        View orderPackedConnector;
        @BindView(R.id.connector_to_order_packed_2)
        View orderPackedConnector2;
        @BindView(R.id.connector_to_dispatched)
        View orderDispatchedConnector;
        @BindView(R.id.connector_to_dispatched_2)
        View orderDispatchedConnector2;
        @BindView(R.id.connector_to_received)
        View orderReceivedConnector;
        @BindView(R.id.connector_to_received_2)
        View orderReceivedConnector2;


        @BindView(R.id.received_order_layout)
        LinearLayout receivedOrderLayout;
        @BindView(R.id.estimatedDeliveryTextView)
        TextView estimatedDeliveryText;
        @BindView(R.id.orders_options)
        ImageButton orderOptions;

        public OrderItemsViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public void setOrderTrackData(OrderItemsViewholder holder,
                                  GetPaymentsList.ResponseValue.PaymentItem paymentItem,
                                  GetPaymentsList.ResponseValue.Order order) {


        GetPaymentsList.ResponseValue.Order orderItem = order;

        String orderStatus = orderItem.getStatus();

        String link = order.getLink();

        holder.trackHereButton.setVisibility(link == null || link.isEmpty() ? View.GONE : View.VISIBLE);
        holder.estimatedDeliveryText.setText(orderItem.getEstimatedDeliveryText());

        holder.orderImagesRecyclerView.setAdapter(new OrderImageAdapter(mActivity, orderItem.getItems()));
        holder.orderImagesRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        holder.orderImagesRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1, RecyclerView.HORIZONTAL, false));

        String approvedDate = DateUtils.getDateForOrderProgress(orderItem.getTracker().getApprovedDate());
        String stockDate = DateUtils.getDateForOrderProgress(orderItem.getTracker().getStockDate());
        String closeDate = DateUtils.getDateForOrderProgress(orderItem.getTracker().getClosedDate());
        String dispatchDate = DateUtils.getDateForOrderProgress(orderItem.getTracker().getDispatchedDate());

        int currentStep = orderItem.getTracker().getStep();
        boolean isRefunded = orderStatus.toLowerCase().contains(mActivity.getString(R.string.refunded));
        String orderText = mActivity.getResources().getString(R.string.order);
        for(int i = Math.abs(currentStep); i > 0; i--) {
            boolean isCurrentStep = isRefunded && i == Math.abs(currentStep);
            switch (i) {
                case ORDER_DATE_ACTIVE_STATE:
                    holder.orderDateGraphNodeView.setBackgroundResource(isCurrentStep ? R.drawable.bg_orders_negative_state :R.drawable.bg_orders_graph_active_state);
                    holder.orderDateGraphNodeView.setText("");
                    holder.orderFirstNodeStatusTextView.setTextColor(mActivity.getResources().getColor(isCurrentStep ? R.color.refunded_state_color : R.color.text_medium));
                    break;

                case ORDER_STOCK_ARRIVED_ACTIVE_STATE:
                    holder.stockArrivedGraphNodeView.setBackgroundResource(isCurrentStep ? R.drawable.bg_orders_negative_state :R.drawable.bg_orders_graph_active_state);
                    holder.stockArrivedGraphNodeView.setText("");
                    holder.orderSecondNodeStatusTextView.setTextColor(mActivity.getResources().getColor(isCurrentStep ? R.color.refunded_state_color : R.color.text_medium));
                    break;

                case ORDER_PACKED_ACTIVE_STATE:
                    holder.orderPackedGraphNodeView.setBackgroundResource(isCurrentStep ? R.drawable.bg_orders_negative_state :R.drawable.bg_orders_graph_active_state);
                    holder.orderPackedGraphNodeView.setText("");
                    holder.orderThirdNodeStatusTextView.setTextColor(mActivity.getResources().getColor(isCurrentStep ? R.color.refunded_state_color : R.color.text_medium));
                    break;

                case ORDER_DISPATCHED_ACTIVE_STATE:
                    holder.dispatchedGraphNodeTextView.setBackgroundResource(isCurrentStep ? R.drawable.bg_orders_negative_state :R.drawable.bg_orders_graph_active_state);
                    holder.dispatchedGraphNodeTextView.setText("");
                    holder.orderFourthNodeStatusTextView.setTextColor(mActivity.getResources().getColor(isCurrentStep ? R.color.refunded_state_color : R.color.text_medium));
                    break;
            }

            holder.orderDateValueTextView.setText(approvedDate);
            holder.stockArrivedValueTextView.setText(stockDate);
            holder.dispatchedDateValueTextView.setText(closeDate);
            holder.orderPackedValueTextView.setText(dispatchDate);
        }

        switch (currentStep) {
            case ORDER_DATE_ACTIVE_STATE:
            case ORDER_DATE_NEGATIVE_STATE:
                if (orderStatus.equalsIgnoreCase("approved")) {
                    orderStatus = "Date";
                }
                holder.orderFirstNodeStatusTextView.setText(String.format(orderText, orderStatus));
                break;

            case ORDER_STOCK_ARRIVED_ACTIVE_STATE:
            case ORDER_STOCK_ARRIVED_NEGATIVE_STATE:
                holder.orderSecondNodeStatusTextView.setText(String.format(orderText, orderStatus));
                break;

            case ORDER_PACKED_ACTIVE_STATE:
            case ORDER_PACKED_NEGATIVE_STATE:
                holder.orderThirdNodeStatusTextView.setText(String.format(orderText, orderStatus));
                break;

            case ORDER_DISPATCHED_ACTIVE_STATE:
            case ORDER_DISPATCHED_NEGATIVE_STATE:
                holder.orderFourthNodeStatusTextView.setText(String.format(orderText, orderStatus));
                break;
        }

        if (currentStep == ORDER_DISPATCHED_ACTIVE_STATE) {
            holder.receivedOrderLayout.setVisibility(View.VISIBLE);
            holder.orderReceivedConnector.setVisibility(View.VISIBLE);
            holder.receivedGraphNodeTextView.setOnClickListener(v -> {
                mClickListener.callOrderReceived(order.getOrderID());
                setActiveOrderReceived(holder, orderText);
            });
        }

        if (order.getActions().contains(ActionConstants.ORDER_RECEIVED_STATUS)) {
            holder.receivedOrderLayout.setVisibility(View.VISIBLE);
            holder.orderReceivedConnector.setVisibility(View.VISIBLE);
            setActiveOrderReceived(holder, orderText);
        }

    }

    private void setActiveOrderReceived(OrderItemsViewholder holder, String orderText) {
        holder.receivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
        holder.receivedGraphNodeTextView.setText("");
        holder.orderFifthNodeStatusTextView.setText(String.format(orderText,mActivity.getResources().getString(R.string.received)));
    }
}