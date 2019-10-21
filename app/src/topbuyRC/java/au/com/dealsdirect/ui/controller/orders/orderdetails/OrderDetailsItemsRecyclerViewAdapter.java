package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.ApplyAddressResponse;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsItemsRecyclerViewAdapter extends RecyclerView.Adapter<OrderDetailsItemsRecyclerViewAdapter.OrderDetailsItemViewHolder> {

    public ArrayList<GetPaymentsList.ResponseValue.Order> mOrderList = new ArrayList<>();
    GetOrderPaymentDetails.ResponseValue.Value mOrderDetail;
    public int mItemPosition;

    Context mContext;

    public OrderDetailsItemsRecyclerViewAdapter(
            Context context,
            int position,
            GetOrderPaymentDetails.ResponseValue.Value orderDetail,
            ArrayList<GetPaymentsList.ResponseValue.Order> orderList) {

        this.mOrderList = orderList;
        this.mContext = context;
        this.mOrderDetail = orderDetail;
        this.mItemPosition = position;
    }

    @Override
    public OrderDetailsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_order_details, parent, false);
        return new OrderDetailsItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderDetailsItemViewHolder holder, final int position) {

        GetPaymentsList.ResponseValue.Order order = mOrderList.get(mItemPosition);
        List<GetOrderPaymentDetails.ResponseValue.Item> orderDetailsItems = mOrderDetail.getOrders().get(mItemPosition).getItems();

        String orderName = orderDetailsItems.get(position).getItem();
        String orderDescription = order.getDescription();
        int orderItemCount = 0;
        String productSize = "";

        if (mOrderDetail != null) {

            String brandId = orderDetailsItems.get(position).getBrandID();
            String imageId = orderDetailsItems.get(position).getImageID();
            String fileName = orderDetailsItems.get(position).getFileName();
            orderItemCount = orderDetailsItems.get(position).getQty();
            productSize = orderDetailsItems.get(position).getSize();

            holder.productPriceTextView.setText(PriceUtils.getPriceStringValue(orderDetailsItems.get(position).getPrice()));

            if (productSize != null) {
                holder.productSizeTextView.setText(productSize);
            }

            ImageUtils.loadImage(mContext, LegacyStringImageUtils.generateImageUrl(brandId, imageId, fileName),
                    holder.productImageView);

        }

        holder.productNameTextView.setText(orderName);
        holder.productDescriptionTextView.setText(orderDescription);
        holder.productQuantityTextView.setText(String.valueOf(orderItemCount));
        holder.productSubtotalTextView.setText(String.format(mContext.getString(R.string.dollar),
                String.valueOf(orderDetailsItems.get(position).getSubTotal().getItemsAmount())));
    }

    @Override
    public int getItemCount() {
        return mOrderDetail.getOrders().get(mItemPosition).getItems() == null ? 0 :
                mOrderDetail.getOrders().get(mItemPosition).getItems().size();
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

        @BindView(R.id.controller_order_details_item_product_name_textview)
        TextView productNameTextView;

        @BindView(R.id.controller_order_details_item_description_textview)
        TextView productDescriptionTextView;

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

        public OrderDetailsItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
