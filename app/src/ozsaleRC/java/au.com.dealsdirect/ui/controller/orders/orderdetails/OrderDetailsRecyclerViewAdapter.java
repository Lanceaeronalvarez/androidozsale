package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.ui.controller.orders.orders.OrderItemClickListener;
import au.com.dealsdirect.utils.ActionConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> mData;

    public static final int VIEW_TYPE_SALE_NAME = 100;
    public static final int VIEW_TYPE_SALE_DETAILS = 101;
    public static final int VIEW_TYPE_SALE_TRACK = 102;
    private OrderDetailsClickListener mClickListener;
    private Activity mActivity;

    private static final int ORDER_DATE_ACTIVE_STATE = 1;
    private static final int ORDER_DATE_NEGATIVE_STATE = -1;
    private static final int ORDER_STOCK_ARRIVED_ACTIVE_STATE = 2;
    private static final int ORDER_STOCK_ARRIVED_NEGATIVE_STATE = -2;
    private static final int ORDER_PACKED_ACTIVE_STATE = 3;
    private static final int ORDER_PACKED_NEGATIVE_STATE = -3;
    private static final int ORDER_DISPATCHED_ACTIVE_STATE = 4;
    private static final int ORDER_DISPATCHED_NEGATIVE_STATE = -4;
    private static final int ORDER_RECEIVED_ACTIVE_STATE = 5;

    private HashMap<String, String> mStatus = new HashMap<>();
    private String mLink;
    private HashMap<String, String> mEstimatedDelivery = new HashMap<>();
    private String mItemName = "";
    private String mItemAddress = "";
    private int mInvoiceNumber;
    private GetOrderPaymentDetails.ResponseValue.Tracker mTracker;
    private String mOrderId = "";
    private String currentStatus = "";
    private boolean hasReceivedStatus = false;
    private String getReceiveDate = "";
    private boolean isOrderCancelled = false;

    public OrderDetailsRecyclerViewAdapter(Activity mActivity,
                                           GetOrderPaymentDetails.ResponseValue.Value orderDetails,
                                           OrderDetailsClickListener clickListener,
                                           HashMap<String, String> status, String link, HashMap<String, String> estimatedDelivery) {

        mClickListener = clickListener;
        this.mActivity = mActivity;
        this.mStatus = status;
        this.mLink = link;
        this.mEstimatedDelivery = estimatedDelivery;
        ordersTransformation(orderDetails);

    }

    public void ordersTransformation(GetOrderPaymentDetails.ResponseValue.Value orderDetails) {
        mData = new ArrayList<>();
        for (GetOrderPaymentDetails.ResponseValue.Order order : orderDetails.getOrders()) {
            mData.add(order);
            for (int i = 0; i < order.getItems().size(); i++) {
                mData.add(order.getItems().get(i));
            }
            mData.add(order.getTracker());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_SALE_NAME:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_details_container, parent, false);
                return new OrderSaleName(v);
            case VIEW_TYPE_SALE_TRACK:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_orders_track, parent, false);
                return new OrderItemTrack(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_order_details, parent, false);
                return new OrderDetailsItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mData.get(position) instanceof GetOrderPaymentDetails.ResponseValue.Item) {
            setOrderDetailsViewHolderData((OrderDetailsItemViewHolder) holder, (GetOrderPaymentDetails.ResponseValue.Item) mData.get(position));
        }

        if (mData.get(position) instanceof GetOrderPaymentDetails.ResponseValue.Order) {
            mItemName = String.valueOf(((GetOrderPaymentDetails.ResponseValue.Order) mData.get(position)).getDescription());
            mItemAddress = ((GetOrderPaymentDetails.ResponseValue.Order) mData.get(position)).getDeliveryAddress();
            mTracker = ((GetOrderPaymentDetails.ResponseValue.Order) mData.get(position)).getTracker();
            mInvoiceNumber = ((GetOrderPaymentDetails.ResponseValue.Order) mData.get(position)).getInvoiceNo();
            mOrderId = ((GetOrderPaymentDetails.ResponseValue.Order) mData.get(position)).getOrderID();
            hasReceivedStatus = ((GetOrderPaymentDetails.ResponseValue.Order) mData.get(position)).getActions()
                    .contains(ActionConstants.ORDER_RECEIVED_STATUS);
            getReceiveDate = ((GetOrderPaymentDetails.ResponseValue.Order) mData.get(position)).getReceived();

            String invoiceNumber = mActivity.getResources().getString(R.string.order_invoice) +" "+ mInvoiceNumber;
            ((OrderSaleName) holder).saleName.setText(invoiceNumber);
            ((OrderSaleName) holder).address.setText(mItemAddress);

            GetOrderPaymentDetails.ResponseValue.Order order = (GetOrderPaymentDetails.ResponseValue.Order) mData.get(position);

            // determine actions to show in bottom dialog
            if (order.getActions().contains(ActionConstants.ORDER_ACTION_CHANGE_ADDRESS) ||
                    order.getActions().contains(ActionConstants.ORDER_ACTION_CHECK_STATUS)) {
                ((OrderSaleName) holder).moreOptions.setVisibility(View.VISIBLE);
            } else {
                ((OrderSaleName) holder).moreOptions.setVisibility(View.GONE);
            }

            HashMap<String, String> itemArrays = new HashMap<>();
            itemArrays.put(ActionConstants.ORDER_INVOICE_NUMBER, String.valueOf(mInvoiceNumber));
            itemArrays.put(ActionConstants.ORDER_ITEM_DESCRIPTION, order.getDescription());
            itemArrays.put(ActionConstants.ORDER_ORDER_ID, order.getOrderID());
            itemArrays.put(ActionConstants.ORDER_REASON, "");

            ((OrderSaleName) holder).moreOptions.setOnClickListener(v -> mClickListener.showOrderDialog(v, (ArrayList<String>) order.getActions(), itemArrays));
        }

        if (mData.get(position) instanceof GetOrderPaymentDetails.ResponseValue.Tracker) {

            Iterator statusIterator = mStatus.keySet().iterator();
            while(statusIterator.hasNext()) {
                String key=(String)statusIterator.next();
                String value=(String)mStatus.get(key);

                if (key.equalsIgnoreCase(mOrderId)) {
                    currentStatus = value;
                }
            }
            setOrderTrackData((OrderItemTrack) holder, mTracker);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        if (mData.get(position) instanceof GetOrderPaymentDetails.ResponseValue.Item) {
            viewType = VIEW_TYPE_SALE_DETAILS;
        } else if (mData.get(position) instanceof GetOrderPaymentDetails.ResponseValue.Tracker) {
            viewType = VIEW_TYPE_SALE_TRACK;
        } else {
            viewType = VIEW_TYPE_SALE_NAME;
        }
        return viewType;
    }

    public void setOrderDetailsViewHolderData(OrderDetailsItemViewHolder holder, GetOrderPaymentDetails.ResponseValue.Item item) {


        String brandId = item.getBrandID();
        String imageId = item.getImageID();
        String fileName = item.getFileName();
        int orderItemCount = item.getQty();
        String productSize = item.getSize();

        holder.productPriceTextView.setText(PriceUtils.getPriceStringValue(item.getPrice()));

        if (productSize != null) {
            holder.productSizeTextView.setText(productSize);
        }

        ImageUtils.loadImage(LegacyStringImageUtils.generateImageUrl(brandId, imageId, fileName),
                holder.productImageView);

        if (item.getActions().contains(ActionConstants.ORDER_ITEM_CANCELLED)) {
            holder.productCancelledTextView.setVisibility(View.VISIBLE);
            isOrderCancelled = true;
        }


        holder.productNameTextView.setText(item.getItem());
        holder.productQuantityTextView.setText(String.valueOf(orderItemCount));
        holder.productSubtotalTextView.setText(PriceUtils.getPriceStringValue(Double.valueOf(item.getSubTotal().getItemsAmount())));

        if (item.getActions().size() == 0) {
            holder.moreOptionsImageButton.setVisibility(View.GONE);
        } else {

            if (item.getActions().contains(ActionConstants.ORDER_ITEM_VIEW_RETURN) ||
                item.getActions().contains(ActionConstants.ORDER_ITEM_RETURN) ||
                item.getActions().contains(ActionConstants.ORDER_ITEM_ACTION_REFUND)) {

                holder.moreOptionsImageButton.setVisibility(View.VISIBLE);

            } else {
                holder.moreOptionsImageButton.setVisibility(View.GONE);
            }

            HashMap<String, String> itemHashMap = new HashMap<>();
            itemHashMap.put(ActionConstants.ORDER_ITEM_RETURN_ID, item.getReturnID());
            itemHashMap.put(ActionConstants.ORDER_ITEM_NAME, item.getItem());
            itemHashMap.put(ActionConstants.ORDER_ITEM_DESCRIPTION, mItemName);
            itemHashMap.put(ActionConstants.ORDER_PRODUCT_ID, item.getID());
            itemHashMap.put(ActionConstants.ORDER_INVOICE_NUMBER, String.valueOf(mInvoiceNumber));
            itemHashMap.put(ActionConstants.ORDER_ITEM_IMAGE_URL, LegacyStringImageUtils.generateImageUrl(brandId, imageId, fileName));
            itemHashMap.put(ActionConstants.ORDER_REASON, "");
            itemHashMap.put(ActionConstants.ORDER_QUANTITY, String.valueOf(item.getQty()));
            itemHashMap.put(ActionConstants.ORDER_SUBTOTAL_ITEM, String.valueOf(item.getSubTotal().getItemsCount()));

            holder.moreOptionsImageButton.setOnClickListener(v -> {
                    mClickListener.showOrderDialog(v, (ArrayList<String>) item.getActions(), itemHashMap);
            });

        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    static class OrderSaleName extends RecyclerView.ViewHolder {
        @BindView(R.id.sale_item_text_value)
        TextView saleName;

        @BindView(R.id.order_details_address_text)
        TextView address;

        @BindView(R.id.img_button_layout)
        RelativeLayout moreOptions;

        public OrderSaleName(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OrderItemTrack extends RecyclerView.ViewHolder {
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

        public OrderItemTrack(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OrderDetailsItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.controller_order_details_item_product_name_textview)
        TextView productNameTextView;

        @BindView(R.id.controller_order_details_item_quantity_textview)
        TextView productQuantityTextView;

        @BindView(R.id.controller_order_details_item_price_textview)
        TextView productPriceTextView;

        @BindView(R.id.controller_order_details_item_size_textview)
        TextView productSizeTextView;

        @BindView(R.id.controller_order_details_item_subtotal_textview)
        TextView productSubtotalTextView;

        @BindView(R.id.controller_order_details_item_imageview)
        ImageView productImageView;

        @BindView(R.id.controller_order_details_cancelled_text)
        TextView productCancelledTextView;

        @BindView(R.id.img_button_layout)
        RelativeLayout moreOptionsImageButton;

        public OrderDetailsItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public void setOrderTrackData(OrderItemTrack holder,
                                  GetOrderPaymentDetails.ResponseValue.Tracker tracker) {


        holder.trackHereButton.setVisibility(mLink == null || mLink.isEmpty() ? View.GONE : View.VISIBLE);

        Iterator keyIterator = mEstimatedDelivery.keySet().iterator();
        while(keyIterator.hasNext()) {
            String key=(String)keyIterator.next();
            String value=(String)mEstimatedDelivery.get(key);

            if (key.equalsIgnoreCase(mOrderId)) {
                holder.estimatedDeliveryText.setText(value);
            }
        }

        String approvedDate = DateUtils.getDateForOrderProgress(tracker.getApprovedDate());
        String stockDate = DateUtils.getDateForOrderProgress(tracker.getStockDate());
        String closeDate = DateUtils.getDateForOrderProgress(tracker.getClosedDate());
        String dispatchDate = DateUtils.getDateForOrderProgress(tracker.getDispatchedDate());
        String receivedDate = (getReceiveDate == null || getReceiveDate.isEmpty()) ?
                "" : DateUtils.getDateForOrderProgress(getReceiveDate);

        int currentStep = tracker.getStep();
        boolean isRefunded = currentStatus.toLowerCase().contains(mActivity.getString(R.string.refunded));
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
            holder.receivedDateValueTextView.setText(receivedDate);
        }

        switch (currentStep) {
            case ORDER_DATE_ACTIVE_STATE:
            case ORDER_DATE_NEGATIVE_STATE:
                if (currentStatus.equalsIgnoreCase("approved")) {
                    currentStatus = "Date";
                }

                if (isOrderCancelled) {
                    currentStatus = mActivity.getString(R.string.cancelled);
                }
                holder.orderFirstNodeStatusTextView.setText(String.format(orderText, currentStatus));
                break;

            case ORDER_STOCK_ARRIVED_ACTIVE_STATE:
            case ORDER_STOCK_ARRIVED_NEGATIVE_STATE:
                holder.orderSecondNodeStatusTextView.setText(String.format(orderText, currentStatus));
                break;

            case ORDER_PACKED_ACTIVE_STATE:
            case ORDER_PACKED_NEGATIVE_STATE:
                holder.orderThirdNodeStatusTextView.setText(String.format(orderText, currentStatus));
                break;

            case ORDER_DISPATCHED_ACTIVE_STATE:
            case ORDER_DISPATCHED_NEGATIVE_STATE:
                holder.orderFourthNodeStatusTextView.setText(String.format(orderText, currentStatus));
                break;
        }

        if (currentStep == ORDER_DISPATCHED_ACTIVE_STATE && !hasReceivedStatus) {
            holder.receivedOrderLayout.setVisibility(View.VISIBLE);
            holder.orderReceivedConnector.setVisibility(View.VISIBLE);
            holder.receivedGraphNodeTextView.setOnClickListener(v -> {
                mClickListener.callOrderReceived(mOrderId);
                setActiveOrderReceived(holder, orderText);
            });
        }

        if (hasReceivedStatus) {
            holder.receivedOrderLayout.setVisibility(View.VISIBLE);
            holder.orderReceivedConnector.setVisibility(View.VISIBLE);
            setActiveOrderReceived(holder, orderText);
        }

    }

    private void setActiveOrderReceived(OrderItemTrack holder, String orderText) {
        holder.receivedGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
        holder.receivedGraphNodeTextView.setText("");
        holder.orderFifthNodeStatusTextView.setText(String.format(orderText,mActivity.getResources().getString(R.string.received)));
    }
}
