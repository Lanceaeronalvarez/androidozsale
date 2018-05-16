package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.PriceUtils;
import timber.log.Timber;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private View mView;
    private GetOrderPaymentDetails.ResponseValue.Value mOrderDetails;
    private View.OnClickListener mOnClickListener;


    private ArrayList<GetPaymentsList.ResponseValue.Order> mOrderList = new ArrayList<>();
    private GetPaymentsList.ResponseValue.Total mTotal;
    private RecyclerView mItemsRecyclerView;

    private Context mContext;
    private int mPaymentReferenceNo;

    public OrderDetailsRecyclerViewAdapter(
            GetOrderPaymentDetails.ResponseValue.Value orderDetails,
            int paymentReferenceNo,
            ArrayList<GetPaymentsList.ResponseValue.Order> orderList,
            GetPaymentsList.ResponseValue.Total total,
            Context context) {

        this.mOrderList = orderList;
        this.mContext = context;
        this.mPaymentReferenceNo = paymentReferenceNo;
        this.mOrderDetails = orderDetails;
        this.mTotal = total;
    }

    @Override
    public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_order_details, parent, false);
        OrdersViewHolder holder = new OrdersViewHolder(v);
        mView = v;

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {

        mItemsRecyclerView = (RecyclerView)
                mView.findViewById(R.id.order_items_recyclerview);

        mItemsRecyclerView.setAdapter(new OrderDetailsItemsRecyclerViewAdapter(
                mContext,
                position,
                mOrderDetails,
                mOrderList));

        mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager
                .VERTICAL, false));


        GetPaymentsList.ResponseValue.Order item = mOrderList.get(position);
        OrdersViewHolder holder = (OrdersViewHolder) vh;

        if (position == mOrderList.size() - 1) {
            holder.orderDetailLayout.setVisibility(View.VISIBLE);
        } else {
            holder.orderDetailLayout.setVisibility(View.GONE);
        }

        String approvedDate = DateUtils.getDateForOrderProgress(item.getTracker().getApprovedDate());
        String approvedTime = DateUtils.getTimeFromDateString(item.getTracker().getApprovedDate());
        String dispatchDate = DateUtils.getDateForOrderProgress(item.getTracker()
                .getDispatchedDate());
        String closeDate = DateUtils.getDateForOrderProgress(item.getTracker().getClosedDate());
        String stockDate = DateUtils.getDateForOrderProgress(item.getTracker().getStockDate());

        holder.orderNumberValueTextView.setText(Integer.toString(mOrderDetails.getPaymentReferenceNo()));

        holder.orderStatusTextView.setText(item.getStatus());
        holder.deliveryAmountValue.setText(PriceUtils.getPriceStringValue(mTotal.getDeliveryAmount()));

        String orderDateValue
                = DateUtils.getTrimmedServerDateString(item.getTracker().getApprovedDate());

        holder.orderDateTextView.setText(orderDateValue + " at " + approvedTime);
        String dateString = item.getEstimatedDeliveryText();
        holder.orderEstimatedDeliveryDateTextView.setText(dateString.substring(dateString.indexOf(":") + 1));

//        holder.orderStatusTextView.setText(mOrderDetails.getLabels() );

        holder.deliveryTotalItemPayment
                .setText(PriceUtils.getPriceStringValue(mOrderDetails.getTotal().getItemsAmount()));
        holder.deliveryTextValue
                .setText(mOrderDetails.getOrders().get(position).getDeliveryAddress());
        holder.orderCreditCardPayment
                .setText(PriceUtils.getPriceStringValue(mTotal.getCreditCardAmount()));

        holder.orderDiscount.setText(
                PriceUtils.getPriceStringValue(mTotal.getDiscountAmount()));

        holder.orderGrandTotal.setText(
                PriceUtils.getPriceStringValue(mTotal.getTotalAmount()));

        holder.orderDateValueTextView.setText(approvedDate);
        holder.stockArrivedValueTextView.setText(stockDate);
        holder.dispatchedDateValueTextView.setText(closeDate);
        holder.orderPackedValueTextView.setText(dispatchDate);


        int currentStep = item.getTracker().getStep();
        int colorActive = holder.itemView.getResources().getColor(R.color.colorAccent);
        String orderStatus = item.getStatus();

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

        String trackHereLink = item.getLink();
        mOnClickListener = view -> {

            Timber.d("OrderDetails", "track here button");
            if (!trackHereLink.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trackHereLink));
                mContext.startActivity(browserIntent);
            } else {
                Timber.d("OrderDetails", "track here link is empty");
            }
        };

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
        mOrderList = new ArrayList<>(orders);
        notifyDataSetChanged();
    }

    static class OrdersViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumberValueTextView;
        TextView orderProductNameTextView;
        TextView orderProductQuantityTextView;

        TextView orderStatusTextView;
        TextView orderDateTextView;
        TextView orderEstimatedDeliveryDateTextView;

        TextView orderCreditCardPayment;
        TextView orderDiscount;
        TextView orderGrandTotal;

        TextView deliveryAmountValue;
        TextView deliveryTextValue;
        TextView deliveryTotalItemPayment;

        LinearLayout orderDetailLayout;

        ImageView orderImage;
        TextView productPrice;

        TextView orderDateGraphNodeTextView;
        TextView orderDateValueTextView;

        TextView stockArrivedGraphNodeTextView;
        TextView stockArrivedValueTextView;

        TextView orderPackedGraphNodeTextView;
        TextView orderPackedValueTextView;

        TextView dispatchedDateValueTextView;
        TextView dispatchedGraphNodeTextView;

        View orderStockArrivedConnector;
        View orderPackedConnector;
        View orderDispatchedConnector;

        TextView orderFirstNodeStatus;
        TextView orderSecondNodeStatus;
        TextView orderThirdNodeStatus;
        TextView orderFourthNodeStatus;

        public OrdersViewHolder(View itemView) {
            super(itemView);

            orderStockArrivedConnector = itemView.findViewById(R.id.connector_to_stock_arrived);
            orderPackedConnector = itemView.findViewById(R.id.connector_to_order_packed);
            orderDispatchedConnector = itemView.findViewById(R.id.connector_to_dispatched);

            orderImage = (ImageView) itemView.findViewById(R.id.controller_order_details_item_imageview);
            productPrice = (TextView) itemView.findViewById(R.id.controller_order_details_item_price_textview);

            orderDetailLayout = (LinearLayout) itemView.findViewById(R.id.product_list_order_detail);

            orderNumberValueTextView = (TextView) itemView.findViewById(R.id.order_number_text_value);
            orderProductNameTextView = (TextView) itemView.findViewById(R.id.controller_order_details_item_product_name_textview);
            orderProductQuantityTextView = (TextView) itemView.findViewById(R.id.controller_order_details_item_quantity_textview);

            deliveryAmountValue = (TextView) itemView.findViewById(R.id.delivery_amount_value);
            deliveryTextValue = (TextView) itemView.findViewById(R.id.delivery_text_view);
            deliveryTotalItemPayment = (TextView) itemView.findViewById(R.id.total_item_payment_value);

            orderStatusTextView = (TextView) itemView.findViewById(R.id.status_text_view);
            orderDateTextView = (TextView) itemView.findViewById(R.id.order_date_text_view);
            orderEstimatedDeliveryDateTextView = (TextView) itemView.findViewById(R.id.estimated_delivery_date_text_view);

            orderCreditCardPayment = (TextView) itemView.findViewById(R.id.credit_card_payment_value);
            orderDiscount = (TextView) itemView.findViewById(R.id.discount_value);
            orderGrandTotal = (TextView) itemView.findViewById(R.id.grand_total_value);

        }
    }
}
