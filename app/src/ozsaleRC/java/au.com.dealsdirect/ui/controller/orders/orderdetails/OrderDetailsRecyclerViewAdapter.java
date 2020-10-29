package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse.Order;
import au.com.dealsdirect.ui.controller.orders.tracking.OrderTrackingClickListener;
import au.com.dealsdirect.ui.controller.orders.tracking.OrderTrackingView;
import au.com.dealsdirect.utils.ActionConstants;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_SALE_NAME = 100;
    private static final int VIEW_TYPE_SALE_DETAILS = 101;
    private static final int VIEW_TYPE_SALE_TRACK = 102;
    private static final int VIEW_TYPE_SALE_SPACER = 0;
    private OrderDetailsClickListener mClickListener;
    private OrderTrackingClickListener mOrderTrackingClickListener;

    private static final int STEP_MAX_COUNT = 5;

    private Order mOrderDetails;
    private List<Item> mData;

    public OrderDetailsRecyclerViewAdapter(Order orderDetails,
                                           OrderDetailsClickListener clickListener,
                                           OrderTrackingClickListener orderTrackingClickListener) {

        mClickListener = clickListener;
        mOrderTrackingClickListener = orderTrackingClickListener;
        this.mOrderDetails = orderDetails;
        flattenData();
    }

    public void replaceData(Order orderDetails) {
        this.mOrderDetails = orderDetails;
        flattenData();
    }

    private void flattenData() {
        mData = new ArrayList<>();
        if (mOrderDetails == null) {
            return;
        }
        for (Order.Invoice invoice : mOrderDetails.getInvoices()) {
            mData.add(new Item(invoice));

            for (Order.Invoice.Product ordered : invoice.getOrdered()) {
                Order.Invoice.Product associatedCancelled = null;
                Order.Invoice.Product associatedReturned = null;
                for (Order.Invoice.Product cancelled : invoice.getCancelled()) {
                    if (cancelled.getId().equals(ordered.getId())) {
                        associatedCancelled = cancelled;
                        break;
                    }
                }
                for (Order.Invoice.Product returned : invoice.getReturned()) {
                    if (returned.getId().equals(ordered.getId())) {
                        associatedReturned = returned;
                        break;
                    }
                }
                mData.add(new Item(invoice.getId(), invoice.getNumber(), new MappedProduct(ordered, associatedCancelled, associatedReturned)));
            }

            // no need to add canceled and returns

            mData.add(new Item(invoice.getId(), invoice.getNumber(), invoice.getDelivery()));
        }
    }

    private static class MappedProduct extends Order.Invoice.Product {
        private Order.Invoice.Product ordered;
        private Order.Invoice.Product cancelled;
        private Order.Invoice.Product returned;

        public MappedProduct(@NonNull Order.Invoice.Product ordered, @Nullable Order.Invoice.Product cancelled, @Nullable Order.Invoice.Product returned) {
            this.ordered = ordered;
            this.cancelled = cancelled;
            this.returned = returned;
        }

        public Float getPrice() {
            return ordered.getPrice();
        }

        public Float getPriceTotal() {
            return ordered.getPriceTotal();
        }

        public String getType() {
            return null;
        }

        public String getId() {
            return ordered.getId();
        }

        public String getName() {
            return ordered.getName();
        }

        public String getBrand() {
            return ordered.getBrand();
        }

        public String getBrandLink() {
            return ordered.getBrandLink();
        }

        public String getProductLink() {
            return ordered.getProductLink();
        }

        public String getImageUrl() {
            return ordered.getImageUrl();
        }

        public String getSize() {
            return ordered.getSize();
        }

        public List<String> getActions() {
            return ordered.getActions();
        }

        public Integer getQuantity() {
            return ordered.getQuantity();
        }

        public String getOrderItemId() {
            return ordered.getOrderItemId();
        }

        public String getReturnId() {
            return returned != null ? returned.getReturnId() : null;
        }

        public Integer getCancelledQuantity() {
            return cancelled != null ? cancelled.getCancelledQuantity() : null;
        }

        public Float getRefundTotal() {
            return cancelled != null ? cancelled.getRefundTotal() : null;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_SALE_NAME:
                return new OrderSaleName(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.row_order_details_container,
                                parent,
                                false));
            case VIEW_TYPE_SALE_TRACK:
                return new OrderItemTrack(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.row_item_orders_track,
                                parent,
                                false));
            case VIEW_TYPE_SALE_DETAILS:
                return new OrderDetailsItemViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.row_item_order_details,
                                parent,
                                false));
            default:
                return new SpacerViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.partial_checkout_item_spacer,
                                parent,
                                false));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = mData.get(position);

        switch (item.getType()) {
            case HEADER:
                ((OrderSaleName) holder).setup(item.getInvoice(), mClickListener);
                break;
            case PRODUCT:
                ((OrderDetailsItemViewHolder) holder).setup(
                        item.getProduct(),
                        item.getInvoiceId(),
                        item.getInvoiceNumber(),
                        mClickListener);
                break;
            case TRACKER:
                ((OrderItemTrack) holder).setup(item.getInvoiceId(), item.getInvoiceNumber(), item.getDelivery(), mOrderTrackingClickListener);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (mData.get(position).type) {
            case HEADER:
                return VIEW_TYPE_SALE_NAME;
            case PRODUCT:
                return VIEW_TYPE_SALE_DETAILS;
            case TRACKER:
                return VIEW_TYPE_SALE_TRACK;
            default:
                return VIEW_TYPE_SALE_SPACER;
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

        @BindView(R.id.orders_options)
        View moreOptions;

        public OrderSaleName(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setup(Order.Invoice invoice, OrderDetailsClickListener listener) {
            String description = "Invoice No: " + invoice.getNumber();
            saleName.setText(description);
            address.setText(invoice.getDelivery().getAddress());

            if (invoice.getActions() != null && !invoice.getActions().isEmpty()) {
                moreOptions.setOnClickListener(v ->
                        listener.showOrderDialog(invoice.getId(), invoice.getNumber(), invoice.getActions()));
                moreOptions.setVisibility(View.VISIBLE);
            } else {
                moreOptions.setOnClickListener(null);
                moreOptions.setVisibility(View.GONE);
            }
        }
    }

    private class SpacerViewHolder extends RecyclerView.ViewHolder {
        SpacerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OrderItemTrack extends RecyclerView.ViewHolder {
        @BindView(R.id.component_order_tracking)
        OrderTrackingView componentOrderTracking;

        OrderItemTrack(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        void setup(String invoiceId, int invoiceNumber, Order.Invoice.Delivery delivery, OrderTrackingClickListener onClickListener) {
            componentOrderTracking.setup(invoiceId, invoiceNumber, delivery, delivery.getTrackingUrl(), onClickListener);
        }
    }

    static class OrderDetailsItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.controller_order_details_item_product_name_textview)
        TextView productNameTextView;

        @BindView(R.id.controller_order_details_item_quantity_textview)
        TextView productQuantityTextView;

        @BindView(R.id.controller_order_details_item_price_textview)
        TextView productPriceTextView;

        @BindView(R.id.product_size_container)
        ViewGroup productSizeContainer;

        @BindView(R.id.controller_order_details_item_size_textview)
        TextView productSizeTextView;

        @BindView(R.id.controller_order_details_item_subtotal_textview)
        TextView productSubtotalTextView;

        @BindView(R.id.controller_order_details_item_imageview)
        ImageView productImageView;

        @BindView(R.id.controller_order_details_cancelled_text)
        TextView productCancelledTextView;

        @BindView(R.id.order_item_details_more)
        View moreOptionsImageButton;

        @BindView(R.id.row_item_order_details_layout)
        RelativeLayout orderDetailsItemLayout;

        @BindView(R.id.controller_order_details_quantity_refunded_textview)
        TextView productQuantityRefunded;

        @BindView(R.id.controller_order_details_subtotal_refunded_textview)
        TextView productSubtotalRefunded;

        OrderDetailsItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        void setup(Order.Invoice.Product product, String invoiceId, int invoiceNumber, OrderDetailsClickListener listener) {
            orderDetailsItemLayout.setVisibility(View.VISIBLE);

            productPriceTextView.setText(PriceUtils.getPriceStringValue(product.getPrice()));

            if (product.getSize() != null && !product.getSize().isEmpty()) {
                productSizeTextView.setText(product.getSize());
                productSizeContainer.setVisibility(View.VISIBLE);
            } else {
                productSizeContainer.setVisibility(View.GONE);
            }

            ImageUtils.loadImage(product.getImageUrl(), productImageView);

            if (product.getActions().contains(ActionConstants.ORDER_ITEM_CANCELLED)) {
                productCancelledTextView.setVisibility(View.VISIBLE);
            }

            productNameTextView.setText(product.getName());
            String quantity = " " + product.getQuantity() + " ";
            productQuantityTextView.setText(quantity);
            String subtotal = " " + PriceUtils.getPriceStringValue(product.getPriceTotal()) + " ";
            productSubtotalTextView.setText(subtotal);

            if (product.getActions() != null &&
                    (product.getActions().contains(ActionConstants.ORDER_ITEM_VIEW_RETURN) ||
                            product.getActions().contains(ActionConstants.ORDER_ITEM_RETURN) ||
                            product.getActions().contains(ActionConstants.ORDER_ITEM_ACTION_REFUND))) {
                moreOptionsImageButton.setOnClickListener(v -> listener.showOrderDialog(invoiceId, invoiceNumber, product));
                moreOptionsImageButton.setVisibility(View.VISIBLE);
            } else {
                moreOptionsImageButton.setOnClickListener(null);
                moreOptionsImageButton.setVisibility(View.GONE);
            }

            if (product.getCancelledQuantity() != null && product.getCancelledQuantity() > 0) {
                productQuantityRefunded.setVisibility(View.VISIBLE);
                productSubtotalRefunded.setVisibility(View.VISIBLE);

                productQuantityRefunded.setText(Integer.toString(product.getQuantity() - product.getCancelledQuantity()));

                productQuantityTextView.setPaintFlags(productSubtotalTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                productSubtotalRefunded.setText(PriceUtils.getPriceStringValue(product.getPriceTotal() - product.getRefundTotal()));

                productSubtotalTextView.setPaintFlags(productSubtotalTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                productQuantityRefunded.setVisibility(View.GONE);
                productSubtotalRefunded.setVisibility(View.GONE);
            }
        }
    }

    private static class Item {
        enum Type {
            HEADER, PRODUCT, TRACKER
        }

        private Type type;

        private String invoiceId = null;
        private int invoiceNumber = 0;
        private Order.Invoice invoice = null;
        private Order.Invoice.Product product = null;
        private Order.Invoice.Delivery delivery = null;

        Item(String invoiceId, int invoiceNumber, Order.Invoice.Product product) {
            type = Type.PRODUCT;
            this.invoiceId = invoiceId;
            this.invoiceNumber = invoiceNumber;
            this.product = product;
        }

        Item(String invoiceId, int invoiceNumber, Order.Invoice.Delivery delivery) {
            type = Type.TRACKER;
            this.invoiceId = invoiceId;
            this.invoiceNumber = invoiceNumber;
            this.delivery = delivery;
        }

        Item(Order.Invoice invoice) {
            type = Type.HEADER;
            this.invoiceId = invoice.getId();
            this.invoiceNumber = invoice.getNumber();
            this.invoice = invoice;
        }

        public Type getType() {
            return type;
        }

        public String getInvoiceId() {
            return invoiceId;
        }

        public int getInvoiceNumber() {
            return invoiceNumber;
        }

        public Order.Invoice getInvoice() {
            return invoice;
        }

        public Order.Invoice.Product getProduct() {
            return product;
        }

        public Order.Invoice.Delivery getDelivery() {
            return delivery;
        }
    }
}
