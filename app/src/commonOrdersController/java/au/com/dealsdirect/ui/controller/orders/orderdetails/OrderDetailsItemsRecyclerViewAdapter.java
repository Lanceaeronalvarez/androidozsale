package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import au.com.dealsdirect.utils.PriceUtils;

/**
 *  Created by smartwave on 22/06/2017.
 */

public class OrderDetailsItemsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<GetPaymentsList.ResponseValue.Order> mOrderList = new ArrayList<>();
    GetOrderPaymentDetails.ResponseValue.Value mOrderDetail;
    public int mItemPosition;

    Context context;
    int paymentReferenceNo;

    public OrderDetailsItemsRecyclerViewAdapter(
            int position,
            GetOrderPaymentDetails.ResponseValue.Value orderDetail,
            ArrayList<GetPaymentsList.ResponseValue.Order> orderList,
            Context context) {

        this.mOrderList = orderList;
        this.context = context;
        this.mOrderDetail = orderDetail;
        this.mItemPosition = position;
    }

    @Override
    public OrderDetailsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_order_details, parent, false);
        OrderDetailsItemViewHolder holder = new OrderDetailsItemViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {

        GetPaymentsList.ResponseValue.Order item = mOrderList.get(mItemPosition);
        OrderDetailsItemViewHolder holder = (OrderDetailsItemViewHolder) vh;

        String orderName = item.getDescription();
        int orderItemCount = 0;
        String productSize = "";

        if (mOrderDetail != null) {

            String brandId = mOrderDetail.getOrders().get(mItemPosition).getItems().get(position).getBrandID();
            String imageId = mOrderDetail.getOrders().get(mItemPosition).getItems().get(position).getImageID();
            String fileName = mOrderDetail.getOrders().get(mItemPosition).getItems().get(position).getFileName();
            orderItemCount = mOrderDetail.getOrders().get(mItemPosition).getItems().get(position).getQty();
            productSize = mOrderDetail.getOrders().get(mItemPosition).getItems().get(position).getSize();

            holder.productPrice.setText(PriceUtils.getPriceStringValue(mOrderDetail.getOrders()
                            .get(mItemPosition).getItems().get(position).getPrice()));

            holder.productSubtotal.setText(PriceUtils.getPriceStringValue(mOrderDetail.getOrders()
                            .get(mItemPosition).getItems().get(position).getSubTotal()
                            .getItemsAmount()));

            if (productSize != null) {
                holder.productSize.setText(productSize);
            }

            ImageUtils.loadImage(LegacyStringImageUtils.generateImageUrl(brandId, imageId, fileName),
                    holder.orderImage);

        }

        holder.orderProductNameTextView.setText(orderName);
        String itemText = " item";
        if (orderItemCount > 1) {
            itemText = " items";
        }
        holder.orderProductQuantityTextView.setText(orderItemCount + itemText);

    }

    @Override
    public int getItemCount() {
        if (mOrderDetail.getOrders().get(mItemPosition).getItems() == null) {
            return 0;
        }

        return mOrderDetail.getOrders().get(mItemPosition).getItems().size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void replace(ArrayList<GetPaymentsList.ResponseValue.Order> orders) {
        mOrderList = orders;
        notifyDataSetChanged();
    }

    static class OrderDetailsItemViewHolder extends RecyclerView.ViewHolder {

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

        TextView productSubtotal;
        TextView productPrice;
        TextView productSize;

        Button trackHereButton;
        LinearLayout orderDetailLayout;

        ImageView orderImage;


        public OrderDetailsItemViewHolder(View itemView) {
            super(itemView);

            trackHereButton = (Button) itemView.findViewById(R.id.order_track_button);

            productSubtotal = (TextView) itemView.findViewById(R.id.controller_order_details_item_subtotal_textview);
            productPrice = (TextView) itemView.findViewById(R.id.controller_order_details_item_price_textview);
            productSize = (TextView) itemView.findViewById(R.id.controller_order_details_item_size_textview);

            orderImage = (ImageView) itemView.findViewById(R.id.controller_order_details_item_imageview);
            orderDetailLayout = (LinearLayout) itemView.findViewById(R.id.product_list_order_detail);

            orderNumberValueTextView = (TextView) itemView.findViewById(R.id.order_number_text_value);
            orderProductNameTextView = (TextView) itemView.findViewById(R.id.controller_order_details_item_product_name_textview);
            orderProductQuantityTextView = (TextView) itemView.findViewById(R.id.controller_order_details_item_quantity_textview);

            approvedDateGraphNodeImageView = (TextView) itemView.findViewById(R.id.order_date_graph_node);
            approvedDateValueTextView = (TextView) itemView.findViewById(R.id.order_date_value);

            stockDateGraphNodeTextView = (TextView) itemView.findViewById(R.id.stock_arrived_graph_node);
            stockDateValueTextView = (TextView) itemView.findViewById(R.id.stock_arrived_value);

            dispatchedDateGraphNodeTextView = (TextView) itemView.findViewById(R.id.order_packed_graph_node);
            dispatchedDateValueTextView = (TextView) itemView.findViewById(R.id.dispatched_date_value);

            closedDateGraphNodeTextView = (TextView) itemView.findViewById(R.id.dispatched_graph_node);
            closedDateValueTextView = (TextView) itemView.findViewById(R.id.order_packed_value);

            orderStatusTextView = (TextView) itemView.findViewById(R.id.status_text_view);
            orderDateTextView = (TextView) itemView.findViewById(R.id.order_date_text_view);
            orderEstimatedDeliveryDateTextView = (TextView) itemView.findViewById(R.id.estimated_delivery_date_text_view);


        }
    }
}
