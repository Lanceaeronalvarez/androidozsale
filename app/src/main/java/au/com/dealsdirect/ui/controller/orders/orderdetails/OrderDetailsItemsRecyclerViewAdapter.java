package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetailsResponse;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import au.com.dealsdirect.utils.PriceUtils;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsItemsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<GetPaymentsList.ResponseValue.Order> mOrderList = new ArrayList<>();
    public GetOrderPaymentDetailsResponse.Value mOrderDetail;
    public int mItemPosition;

    Context context;
    int paymentReferenceNo;

    public OrderDetailsItemsRecyclerViewAdapter(
            int position,
            GetOrderPaymentDetailsResponse.Value orderDetail,
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

//        CURRENCY = Config.getCurrencySign();
        Log.d("myorders", " on bind view holder = " + position);
        //
        //        RecyclerView itemsRecyclerView = (RecyclerView)
        //                mView.findViewById(R.id.order_items_recyclerview);
        //


        GetPaymentsList.ResponseValue.Order item = mOrderList.get(mItemPosition);
        // GetPaymentsList.ResponseValue.Order orderItem = item.getOrders().get(position);
        OrderDetailsItemViewHolder holder = (OrderDetailsItemViewHolder) vh;

        String orderName = item.getDescription();
        int orderItemCount = 0;
        String productSize = "";

        if (mOrderDetail != null) {
            Log.d("myorders", "orderdetails not null");

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

            ImageUtils.loadImage(context,
                    LegacyStringImageUtils.productDetailsImageURLString(brandId, imageId, fileName),
                    holder.orderImage);

//            Glide.with(context).load()
//                    .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESULT)
//                    .placeholder(R.drawable.topbuy_loading_image_placeholder_xml_tall)
//                    .fitCenter().into(holder.orderImage);
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

        Log.d("myorders", "size in items recyclerview adapter inside orders = " +
                mOrderDetail.getOrders().get(mItemPosition).getItems().size());
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

        RelativeLayout orderNumberContainerLayout;

        LinearLayout orderDetailLayout;

        ImageView orderImage;


        public OrderDetailsItemViewHolder(View itemView) {
            super(itemView);

            trackHereButton = (Button) itemView.findViewById(R.id.order_track_button);

            productSubtotal = (TextView) itemView.findViewById(R.id.product_subtotal);
            productPrice = (TextView) itemView.findViewById(R.id.product_price);
            productSize = (TextView) itemView.findViewById(R.id.product_size);

            orderImage = (ImageView) itemView.findViewById(R.id.order_image);
            orderNumberContainerLayout = (RelativeLayout) itemView.findViewById(R.id.order_number_container);
            orderDetailLayout = (LinearLayout) itemView.findViewById(R.id.product_list_order_detail);

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
