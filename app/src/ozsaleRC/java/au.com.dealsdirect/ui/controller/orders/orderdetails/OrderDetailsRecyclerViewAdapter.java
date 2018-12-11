package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
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

    public OrderDetailsRecyclerViewAdapter(GetOrderPaymentDetails.ResponseValue.Value orderDetails) {
        mData = dataTransformation(orderDetails);
    }

    public ArrayList<Object> dataTransformation(GetOrderPaymentDetails.ResponseValue.Value orderDetails) {
        ArrayList<Object> data = new ArrayList<>();
        for (GetOrderPaymentDetails.ResponseValue.Order order : orderDetails.getOrders()) {
            data.add(order.getDescription());
            for (GetOrderPaymentDetails.ResponseValue.Item item : order.getItems()) {
                data.add(item);
            }
        }
        return data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_SALE_NAME:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_sale_name, parent, false);
                return new OrderSaleName(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_order_details, parent, false);
                return new OrderDetailsItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SALE_NAME) {
            ((OrderSaleName) holder).saleName.setText(String.valueOf(mData.get(0)));
        } else {
            setOrderDetailsViewHolderData((OrderDetailsItemViewHolder) holder, (GetOrderPaymentDetails.ResponseValue.Item) mData.get(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        if (mData.get(position) instanceof GetOrderPaymentDetails.ResponseValue.Item) {
            viewType = VIEW_TYPE_SALE_DETAILS;
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


        holder.productNameTextView.setText(item.getItem());
        holder.productQuantityTextView.setText(String.valueOf(orderItemCount));
        holder.productSubtotalTextView.setText(PriceUtils.getPriceStringValue(Double.valueOf(item.getSubTotal().getItemsAmount())));
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

        public OrderSaleName(View itemView) {
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

        public OrderDetailsItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
