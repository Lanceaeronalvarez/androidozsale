package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.PriceUtils;
import timber.log.Timber;

/**
 *  Created by smartwave on 22/06/2017.
 */

public class OrderDetailsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private View mView;
    GetOrderPaymentDetails.ResponseValue.Value mOrderDetails;
    private View.OnClickListener onClickListener;


    public ArrayList<GetPaymentsList.ResponseValue.Order> orderList = new ArrayList<>();
    public GetPaymentsList.ResponseValue.Total total;
    RecyclerView mItemsRecyclerView;

    Context context;
    int paymentReferenceNo;

    public OrderDetailsRecyclerViewAdapter(
            GetOrderPaymentDetails.ResponseValue.Value orderDetails,
            int paymentReferenceNo,
            ArrayList<GetPaymentsList.ResponseValue.Order> orderList,
            GetPaymentsList.ResponseValue.Total total,
            Context context){

        this.orderList = orderList;
        this.context = context;
        this.paymentReferenceNo = paymentReferenceNo;
        this.mOrderDetails = orderDetails;
        this.total = total;
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

        mItemsRecyclerView = (RecyclerView) mView.findViewById(R.id.order_items_recyclerview);

        mItemsRecyclerView.setAdapter(new OrderDetailsItemsRecyclerViewAdapter(context, position, mOrderDetails, orderList));

        mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager
                .VERTICAL, false));


        GetPaymentsList.ResponseValue.Order item = orderList.get(position);
        OrdersViewHolder holder = (OrdersViewHolder) vh;
        
        String orderItemCount = item.getSubTotal().getItemsCount()+"";
        String orderNumber = Integer.toString(paymentReferenceNo);
        String orderName = item.getDescription();
        String orderStatus = item.getStatus();

        if (position == orderList.size()-1){
            holder.orderDetailLayout.setVisibility(View.VISIBLE);
        }else{
            holder.orderDetailLayout.setVisibility(View.GONE);
        }

        String itemText=" item";
        if(Integer.parseInt(orderItemCount) > 1){
            itemText=" items";
        }

        String approvedDate = DateUtils.getDateForOrderProgress(item.getTracker().getApprovedDate());
        String approvedTime = DateUtils.getTimeFromDateString(item.getTracker().getApprovedDate());
        String dispatchDate = DateUtils.getDateForOrderProgress(item.getTracker()
                .getDispatchedDate());
        String closeDate = DateUtils.getDateForOrderProgress(item.getTracker().getClosedDate());
        String stockDate = DateUtils.getDateForOrderProgress(item.getTracker().getStockDate());


        holder.approvedDateValueTextView.setText(approvedDate);
        holder.stockDateValueTextView.setText(stockDate);
        holder.dispatchedDateValueTextView.setText(closeDate);
        holder.closedDateValueTextView.setText(dispatchDate);


        int currentStep = item.getTracker().getStep();
        int colorActive = holder.itemView.getResources().getColor(R.color.colorAccent);

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
                holder.orderSecondNodeStatus.setText(R.string.stock_arrived);
                break;
            case -2:
                holder.stockDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_negative_state);
                holder.stockDateGraphNodeTextView.setText("");
                holder.orderSecondNodeStatus.setText(orderStatus);
                break;

            case 3:
                holder.stockDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockDateGraphNodeTextView.setText("");
                holder.dispatchedDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.dispatchedDateGraphNodeTextView.setText("");
                holder.orderThirdNodeStatus.setText(R.string.order_packed);
                break;

            case -3:
                holder.stockDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockDateGraphNodeTextView.setText("");
                holder.dispatchedDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_negative_state);
                holder.dispatchedDateGraphNodeTextView.setText("");
                holder.orderThirdNodeStatus.setText(orderStatus);
                break;

            case 4:
                holder.stockDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockDateGraphNodeTextView.setText("");
                holder.dispatchedDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.dispatchedDateGraphNodeTextView.setText("");
                holder.closedDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.closedDateGraphNodeTextView.setText("");

                holder.orderFourthNodeStatus.setText(R.string.dispatched);
                break;

            case -4:
                holder.stockDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.stockDateGraphNodeTextView.setText("");
                holder.dispatchedDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_graph_active_state);
                holder.dispatchedDateGraphNodeTextView.setText("");
                holder.closedDateGraphNodeTextView.setBackgroundResource(R.drawable.bg_orders_negative_state);
                holder.closedDateGraphNodeTextView.setText("");
                holder.orderFourthNodeStatus.setText(orderStatus);

                break;


            default:
                break;
        }


        holder.orderStatusTextView.setText(item.getStatus());
        holder.deliveryAmountValue.setText(PriceUtils.getPriceStringValue(total.getDeliveryAmount()));

        String orderDateValue
                = DateUtils.getTrimmedServerDateString(item.getTracker().getApprovedDate());

        holder.orderDateTextView.setText(orderDateValue + " at "+ approvedTime);
        String dateString = item.getEstimatedDeliveryText();
        holder.orderEstimatedDeliveryDateTextView.setText(dateString.substring(dateString.indexOf(":")+1));

        holder.deliveryTotalItemPayment
                .setText(PriceUtils.getPriceStringValue(mOrderDetails.getTotal().getItemsAmount()));
        holder.deliveryTextValue
                .setText(mOrderDetails.getOrders().get(position).getDeliveryAddress());
        holder.orderCreditCardPayment
                .setText(PriceUtils.getPriceStringValue(total.getCreditCardAmount()));

        holder.orderDiscount.setText(
                PriceUtils.getPriceStringValue(total.getDiscountAmount()));

        holder.orderGrandTotal.setText(
                PriceUtils.getPriceStringValue(total.getTotalAmount()));

        String trackHereLink = item.getLink();
        onClickListener = view -> {

            Timber.d("OrderDetails", "track here button");
            if (!trackHereLink.isEmpty()){

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trackHereLink));
                context.startActivity(browserIntent);
            }else {
                Timber.d("OrderDetails", "track here link is empty");
            }
        };

        if (!trackHereLink.isEmpty()){
            holder.trackHereButton.setVisibility(View.VISIBLE);
            holder.trackHereButton.setOnClickListener(onClickListener);

        }else{
            holder.trackHereButton.setVisibility(View.GONE);
            Timber.d("OrderDetails", "track here link is empty");
        }

    }

    @Override public int getItemCount() {
        if (orderList == null){
            return 0;
        }

        return orderList.size();
    }

    @Override public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void replace(ArrayList<GetPaymentsList.ResponseValue.Order> orders){
        orderList = new ArrayList<>(orders);
        notifyDataSetChanged();
    }

    static class OrdersViewHolder extends RecyclerView.ViewHolder {

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

        TextView orderCreditCardPayment;
        TextView orderDiscount;
        TextView orderGrandTotal;

        TextView deliveryAmountValue;
        TextView deliveryTextValue;
        TextView deliveryTotalItemPayment;

        Button trackHereButton;

        RelativeLayout orderNumberContainerLayout;

        LinearLayout orderDetailLayout;

        ImageView orderImage;
        TextView productPrice;
        TextView productSubtotal;

        TextView orderFirstNodeStatus;
        TextView orderSecondNodeStatus;
        TextView orderThirdNodeStatus;
        TextView orderFourthNodeStatus;

        View orderStockArrivedConnector;
        View orderPackedConnector;
        View orderDispatchedConnector;

        public OrdersViewHolder(View itemView) {
            super(itemView);

            trackHereButton = (Button) itemView.findViewById(R.id.order_track_button);

            orderStockArrivedConnector = itemView.findViewById(R.id.my_order_product_order_date_graph_check_connector);
            orderPackedConnector = itemView.findViewById(R.id.my_order_product_stocked_arrived_graph_check_connector);
            orderDispatchedConnector = itemView.findViewById(R.id.my_order_product_packed_graph_check_connector);

            orderDetailLayout = (LinearLayout) itemView.findViewById(R.id.product_list_order_detail);

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

            deliveryAmountValue = (TextView) itemView.findViewById(R.id.delivery_amount_value);
            deliveryTextValue = (TextView) itemView.findViewById(R.id.delivery_text_view);
            deliveryTotalItemPayment = (TextView) itemView.findViewById(R.id.total_item_payment_value);

            orderStatusTextView = (TextView) itemView.findViewById(R.id.status_text_view);
            orderDateTextView = (TextView) itemView.findViewById(R.id.order_date_text_view);
            orderEstimatedDeliveryDateTextView = (TextView) itemView.findViewById(R.id.estimated_delivery_date_text_view);

            orderCreditCardPayment = (TextView) itemView.findViewById(R.id.credit_card_payment_value);
            orderDiscount = (TextView) itemView.findViewById(R.id.discount_value);
            orderGrandTotal = (TextView) itemView.findViewById(R.id.grand_total_value);


            orderFirstNodeStatus = (TextView) itemView.findViewById(R.id.tracker_first_node);
            orderSecondNodeStatus = (TextView) itemView.findViewById(R.id.tracker_second_node);
            orderThirdNodeStatus = (TextView) itemView.findViewById(R.id.tracker_third_node);
            orderFourthNodeStatus = (TextView) itemView.findViewById(R.id.tracker_fourth_node);
        }
    }
}
