package au.com.dealsdirect.ui.controller.orders.orders;

import android.app.Activity;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.utils.ActionConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String SHIP_FROM = "SHIP_FROM";
    private static final String ESTIMATED_DELIVERY = "ESTIMATED_DELIVERY";
    private static final String SHIP_TO = "SHIP_TO";

    public static final int TITLE_VIEW_TYPE = 10;
    public static final int DETAILS_VIEW_TYPE = 11;
    private static final int ORDER_DATE_ACTIVE_STATE = 1;
    private static final int ORDER_DATE_NEGATIVE_STATE = -1;
    private static final int ORDER_STOCK_ARRIVED_ACTIVE_STATE = 2;
    private static final int ORDER_STOCK_ARRIVED_NEGATIVE_STATE = -2;
    private static final int ORDER_PACKED_ACTIVE_STATE = 3;
    private static final int ORDER_PACKED_NEGATIVE_STATE = -3;
    private static final int ORDER_DISPATCHED_ACTIVE_STATE = 4;
    private static final int ORDER_DISPATCHED_NEGATIVE_STATE = -4;
    private static final int ORDER_RECEIVED_ACTIVE_STATE = 5;
    private ArrayList<GetPaymentsList.ResponseValue.PaymentItem> mPaymentItemList;
    private Activity mActivity;
    private OrderItemClickListener mClickListener;
    private ArrayList<Pair<Object, Integer>> mData;
    private ArrayList<String> mReferenceNumbers;
    private GetPaymentsList.ResponseValue.PaymentItem item;
    private LinkedHashMap<String, Object> mLinkedHashMap;
    private HashMap<String, HashMap<String, String>> deliveryRoutes = new HashMap<>();
    private HashMap<String, String> mStatusArray = new HashMap<>();
    private boolean isItemReceived = false;
    private String mLink;

    public OrdersRecyclerViewAdapter(Activity mActivity,
                                     OrderItemClickListener clickListener,
                                     ArrayList<GetPaymentsList.ResponseValue.PaymentItem> paymentItems) {

        this.mPaymentItemList = paymentItems;
        this.mClickListener = clickListener;
        this.mActivity = mActivity;
        flattenData();
        mReferenceNumbers = flattenReferenceNumber(paymentItems);
    }

    @Override
    public int getItemViewType(int position) {
        //TODO: logic for determining what viewholder should be shown
        return mData.get(position).first instanceof GetPaymentsList.ResponseValue.PaymentItem ? TITLE_VIEW_TYPE : DETAILS_VIEW_TYPE;
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
//            item = (GetPaymentsList.ResponseValue.PaymentItem) mData.get(position);
            item = (GetPaymentsList.ResponseValue.PaymentItem) mData.get(position).first;
            ((OrdersViewHolder) viewHolder).orderNumberTextView.setText(String.valueOf(item.getPaymentReferenceNo()));
            ((OrdersViewHolder) viewHolder).orderNumberRightArrowImageView.setOnClickListener(view -> {
                int index = mData.get(viewHolder.getAdapterPosition()).second;
                mClickListener.onOrderItemClick(String.valueOf(item.getPaymentReferenceNo()),
                        mStatusArray, mLink, deliveryRoutes, index);
            });
        } else if (viewType == DETAILS_VIEW_TYPE) {
            GetPaymentsList.ResponseValue.Order order =
                    (GetPaymentsList.ResponseValue.Order) mData.get(position).first;
            setOrderTrackData((OrderItemsViewholder) viewHolder, item, order);
            ((OrderItemsViewholder) viewHolder).trackHereButton.setOnClickListener(v ->
                    mClickListener.onOrderItemTrackingButtonClick(order.getLink(), null));

            if (order.getActions().size() == 0) {
                ((OrderItemsViewholder) viewHolder).orderOptionsLayout.setVisibility(View.GONE);
            } else {

                if (order.getActions().contains(ActionConstants.ORDER_ACTION_CHECK_STATUS) ||
                        order.getActions().contains(ActionConstants.ORDER_ACTION_CHANGE_ADDRESS) ||
                        order.getActions().contains(ActionConstants.ORDER_ACTION_REFUND)) {

                    ((OrderItemsViewholder) viewHolder).orderOptionsLayout.setVisibility(View.VISIBLE);
                }

                HashMap<String, String> itemArrays = new HashMap<>();
                itemArrays.put(ActionConstants.ORDER_ORDER_ID, order.getOrderID());
                itemArrays.put(ActionConstants.ORDER_ITEM_DESCRIPTION, order.getDescription());
                itemArrays.put(ActionConstants.ORDER_INVOICE_NUMBER, order.getInvoiceNo().toString());

                ((OrderItemsViewholder) viewHolder).orderOptionsLayout.setOnClickListener(v ->
                        mClickListener.onOrderItemShowOptions(v, (ArrayList<String>) order.getActions(), itemArrays));
            }

            HashMap<String, String> deliveryRoute = new HashMap<>();
            deliveryRoute.put(ESTIMATED_DELIVERY, order.getEstimatedDeliveryText());
            deliveryRoute.put(SHIP_FROM, order.getShipFrom());
            deliveryRoute.put(SHIP_TO, order.getShipTo());

            deliveryRoutes.put(order.getOrderID(), deliveryRoute);
            mStatusArray.put(order.getOrderID(), order.getStatus());

            mLink = order.getLink();

            viewHolder.itemView.setOnClickListener(view -> {
                int index = mData.get(viewHolder.getAdapterPosition()).second;
                mClickListener.onOrderItemClick(String.valueOf(item.getPaymentReferenceNo()),
                        mStatusArray, order.getLink(), deliveryRoutes, index);
            });
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

    private ArrayList<String> flattenReferenceNumber(ArrayList<GetPaymentsList.ResponseValue.PaymentItem> data) {
        ArrayList<String> referenceNumbers = new ArrayList<>();
        GetPaymentsList.ResponseValue.PaymentItem item = new GetPaymentsList.ResponseValue.PaymentItem();

        for (int i = 0; i < data.size(); i++) {
//            if (data.get(i) instanceof GetPaymentsList.ResponseValue.PaymentItem) {
//                item = (GetPaymentsList.ResponseValue.PaymentItem) data.get(i);
//            }
            referenceNumbers.add(String.valueOf(item.getPaymentReferenceNo()));
        }
        return referenceNumbers;
    }

    private void flattenData() {
        mData = new ArrayList<>();

//        for (GetPaymentsList.ResponseValue.PaymentItem item : mPaymentItemList) {
        for (int h = 0; h < mPaymentItemList.size(); h++) {
            GetPaymentsList.ResponseValue.PaymentItem item = mPaymentItemList.get(h);
            mData.add(new Pair<>(item, h));
            for (int i = 0; i < item.getOrders().size(); i++) {
                mData.add(new Pair<>(item.getOrders().get(i), h));
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

    public void setOrderTrackData(OrderItemsViewholder holder,
                                  GetPaymentsList.ResponseValue.PaymentItem paymentItem,
                                  GetPaymentsList.ResponseValue.Order order) {


        GetPaymentsList.ResponseValue.Order orderItem = order;

        String orderStatus = orderItem.getStatus();

        isItemReceived = orderItem.getReceived() != null;

        String link = order.getLink();

        holder.trackHereButton.setVisibility(link == null || link.isEmpty() ? View.GONE : View.VISIBLE);
        holder.shipFromTextView.setText(StringUtils.twoPartStringWithStyles(
                mActivity.getResources().getString(R.string.ship_from_with_colon),
                null,
                orderItem.getShipFrom(),
                new StyleSpan(BOLD)
        ));
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(orderItem.getEstimatedDeliveryText());
        StringUtils.applySpanToSubstringsMatching(
                stringBuilder,
                new StyleSpan(BOLD),
                "(?!.*:).{1,}",
                SPAN_EXCLUSIVE_INCLUSIVE);
        holder.estimatedDeliveryText.setText(stringBuilder);
        holder.shipToTextView.setText(StringUtils.twoPartStringWithStyles(
                mActivity.getResources().getString(R.string.ship_to_with_colon),
                null,
                orderItem.getShipFrom(),
                new StyleSpan(BOLD)
        ));

        holder.orderImagesRecyclerView.setAdapter(new OrderImageAdapter(mActivity, orderItem.getItems()));
        holder.orderImagesRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        holder.orderImagesRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1, RecyclerView.HORIZONTAL, false));

        String approvedDate = DateUtils.getDateForOrderProgress(orderItem.getTracker().getApprovedDate());
        String stockDate = DateUtils.getDateForOrderProgress(orderItem.getTracker().getStockDate());
        String closeDate = DateUtils.getDateForOrderProgress(orderItem.getTracker().getClosedDate());
        String dispatchDate = DateUtils.getDateForOrderProgress(orderItem.getTracker().getDispatchedDate());
        String receivedDate = orderItem.getReceived() != null ?
                DateUtils.getDateForOrderProgress(orderItem.getReceived()) : "";

        int currentStep = orderItem.getTracker().getStep();
        boolean isRefunded = orderStatus.toLowerCase().contains(mActivity.getString(R.string.refunded));
        String orderText = mActivity.getResources().getString(R.string.order);
        for (int i = Math.abs(currentStep); i > 0; i--) {
            boolean isCurrentStep = isRefunded && i == Math.abs(currentStep);
            switch (i) {
                case ORDER_DATE_ACTIVE_STATE:
                    holder.orderDateGraphNodeView.setBackgroundResource(isCurrentStep ? R.drawable.bg_orders_negative_state : R.drawable.bg_orders_graph_active_state);
                    holder.orderDateGraphNodeView.setText("");
                    holder.orderFirstNodeStatusTextView.setTextColor(mActivity.getResources().getColor(isCurrentStep ? R.color.refunded_state_color : R.color.text_medium));
                    break;

                case ORDER_STOCK_ARRIVED_ACTIVE_STATE:
                    holder.stockArrivedGraphNodeView.setBackgroundResource(isCurrentStep ? R.drawable.bg_orders_negative_state : R.drawable.bg_orders_graph_active_state);
                    holder.stockArrivedGraphNodeView.setText("");
                    holder.orderSecondNodeStatusTextView.setTextColor(mActivity.getResources().getColor(isCurrentStep ? R.color.refunded_state_color : R.color.text_medium));
                    break;

                case ORDER_PACKED_ACTIVE_STATE:
                    holder.orderPackedGraphNodeView.setBackgroundResource(isCurrentStep ? R.drawable.bg_orders_negative_state : R.drawable.bg_orders_graph_active_state);
                    holder.orderPackedGraphNodeView.setText("");
                    holder.orderThirdNodeStatusTextView.setTextColor(mActivity.getResources().getColor(isCurrentStep ? R.color.refunded_state_color : R.color.text_medium));
                    break;

                case ORDER_DISPATCHED_ACTIVE_STATE:
                    holder.dispatchedGraphNodeTextView.setBackgroundResource(isCurrentStep ? R.drawable.bg_orders_negative_state : R.drawable.bg_orders_graph_active_state);
                    holder.dispatchedGraphNodeTextView.setText("");
                    holder.orderFourthNodeStatusTextView.setTextColor(mActivity.getResources().getColor(isCurrentStep ? R.color.refunded_state_color : R.color.text_medium));
                    break;
            }

            holder.orderDateValueTextView.setText(approvedDate);
            holder.stockArrivedValueTextView.setText(stockDate);
            holder.dispatchedDateValueTextView.setText(closeDate);
            holder.orderPackedValueTextView.setText(dispatchDate);
            holder.receivedDateValueTextView.setText(receivedDate);
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

////        Commented for the meantime
//        if (currentStep == ORDER_DISPATCHED_ACTIVE_STATE &&
//                order.getActions().contains(ActionConstants.ORDER_RECEIVED_STATUS)) {
//            holder.receivedOrderLayout.setVisibility(View.VISIBLE);
//            holder.orderReceivedConnector.setVisibility(View.VISIBLE);
//            holder.receivedGraphNodeTextView.setOnClickListener(v -> {
//                mClickListener.callOrderReceived(order.getOrderID());
//                setActiveOrderReceived(holder, orderText);
//            });
//        }
//
//        if (isItemReceived) {
//            holder.receivedOrderLayout.setVisibility(View.VISIBLE);
//            holder.orderReceivedConnector.setVisibility(View.VISIBLE);
//            setActiveOrderReceived(holder, orderText);
//        }

        holder.receivedOrderLayout.setVisibility(View.GONE);
        holder.orderReceivedConnector.setVisibility(View.GONE);

    }

    private void setActiveOrderReceived(OrderItemsViewholder holder, String orderText) {
        holder.receivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
        holder.receivedGraphNodeTextView.setText("");
        holder.orderFifthNodeStatusTextView.setText(String.format(orderText, mActivity.getResources().getString(R.string.received)));
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_number_text_value)
        TextView orderNumberTextView;

        @BindView(R.id.my_order_number_container)
        RelativeLayout orderNumberContainerLayout;

        @BindView(R.id.order_number_right_arrow)
        ImageView orderNumberRightArrowImageView;

        public OrdersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class OrderItemsViewholder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_image_recyclerview)
        RecyclerView orderImagesRecyclerView;
        @BindView(R.id.order_image_options_layout)
        RelativeLayout orderOptionsLayout;

        @BindView(R.id.trackHereButton)
        ViewGroup trackHereButton;

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
        @BindView(R.id.shipFromTextView)
        TextView shipFromTextView;
        @BindView(R.id.shipToTextView)
        TextView shipToTextView;
        @BindView(R.id.orders_options)
        ImageButton orderOptions;

        public OrderItemsViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}