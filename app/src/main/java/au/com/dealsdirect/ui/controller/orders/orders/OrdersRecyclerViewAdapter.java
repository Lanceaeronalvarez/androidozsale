package au.com.dealsdirect.ui.controller.orders.orders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.ui.controller.orders.tracking.OrderTrackingClickListener;
import au.com.dealsdirect.ui.controller.orders.tracking.OrderTrackingView;
import au.com.dealsdirect.utils.ActionConstants;
import au.com.dealsdirect.utils.AppLogger;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER_VIEW_TYPE = 10;
    private static final int DETAILS_VIEW_TYPE = 11;
    private static final int SUBTITLE_VIEW_TYPE = 1;
    private static final int SEPARATOR_VIEW_TYPE = 0;

    private static final int STEP_MAX_COUNT = 5;

    private OrderItemClickListener mClickListener;
    private OrderTrackingClickListener mOrderTrackingClickListener;

    private List<Item> items = new ArrayList<>();

    public OrdersRecyclerViewAdapter(OrderItemClickListener clickListener,
                                     OrderTrackingClickListener orderTrackingClickListener,
                                     List<GetOrdersResponse.Order> orders) {

        this.mClickListener = clickListener;
        this.mOrderTrackingClickListener = orderTrackingClickListener;
        flattenData(orders);
    }

    public void setSubtitle(String subtitle) {
        if (subtitle != null && !subtitle.isEmpty()) {
            if (!items.isEmpty() && items.get(0).getType() == Item.Type.SUBTITLE) {
                items.remove(0);
            }
            items.add(0, new Item(subtitle));
        } else if (!items.isEmpty() && items.get(0).getType() == Item.Type.SUBTITLE) {
            items.remove(0);
        }
    }

    public ItemCounts addData(List<GetOrdersResponse.Order> orders) {
        int oldCount = items.size();
        flattenData(orders);
        int newCount = items.size();
        return new ItemCounts(oldCount, newCount);
    }

    public void clearData() {
        Item subtitleItem = null;
        if (!items.isEmpty()) {
            if (items.get(0).getType() == Item.Type.SUBTITLE) {
                subtitleItem = items.get(0);
            }
        }
        items.clear();
        if (subtitleItem != null) {
            items.add(subtitleItem);
        }
    }

    public Set<Integer> updateItem(GetOrdersResponse.Order order) {
        HashSet<Integer> indices = new HashSet<>();
        for (GetOrdersResponse.Order.Invoice invoice : order.getInvoices()) {
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                if (item.getType() == Item.Type.INVOICE &&
                        item.getInvoice().getNumber().equals(invoice.getNumber())) {
                    Item replacementItem = new Item(item.getIndex(), item.getOrderNumber(), invoice, item.getLocationFilter());
                    items.remove(i);
                    items.add(i, replacementItem);
                    indices.add(i);
                    break;
                }
            }
        }
        return indices;
    }

    private void flattenData(List<GetOrdersResponse.Order> orders) {
        final int lastIndex = getLastIndexOfFlattenedData();

        final SparseArray<String> locationFilterHashes = new SparseArray<>();

        for (int i = 0; i < orders.size(); i++) {
            final int currentIndex = lastIndex + 1 + i;
            final GetOrdersResponse.Order order = orders.get(i);
            final Item headerItem = new Item(currentIndex, order.getNumber());
            items.add(headerItem);
            for (GetOrdersResponse.Order.Shipment shipment : order.getShipments()) {
                if (shipment.getInvoiceNumbers() != null) {
                    final Integer invoiceNumber = shipment.getInvoiceNumbers().get(0);
                    if (invoiceNumber != null) {
                        locationFilterHashes.put(invoiceNumber, shipment.getLocationFilter());
                    }
                }
            }
            for (GetOrdersResponse.Order.Invoice invoice : order.getInvoices()) {
                final Item invoiceItem = new Item(currentIndex, order.getNumber(), invoice, locationFilterHashes.get(invoice.getNumber()));
                items.add(invoiceItem);
            }
            // separator
            items.add(new Item());
        }
    }

    private int getLastIndexOfFlattenedData() {
        for (int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i);
            if (item.hasIndex()) {
                return item.getIndex();
            }
        }
        return -1;
    }

    @Override
    public int getItemViewType(int position) {
        switch (items.get(position).getType()) {
            case HEADER:
                return HEADER_VIEW_TYPE;
            case INVOICE:
                return DETAILS_VIEW_TYPE;
            case SUBTITLE:
                return SUBTITLE_VIEW_TYPE;
            default:
                return SEPARATOR_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_VIEW_TYPE:
                return new OrdersViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.row_order_container,
                                parent, false));
            case DETAILS_VIEW_TYPE:
                return new OrderItemsViewholder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.row_item_orders,
                                parent, false));
            case SUBTITLE_VIEW_TYPE:
                return new SubtitleViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.viewholder_subtitle,
                                parent, false));
            default:
                return new SpacerViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.partial_checkout_item_spacer,
                                parent,
                                false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        int viewType = getItemViewType(position);

        Item item = items.get(position);
        int index = item.getIndex();

        switch (viewType) {
            case HEADER_VIEW_TYPE:
                AppLogger.d("Orders Title Item Position: " + position);
                OrdersViewHolder ordersViewHolder = (OrdersViewHolder) viewHolder;
                ordersViewHolder.orderNumberTextView
                        .setText(String.valueOf(item.getOrderNumber()));
                ordersViewHolder.orderNumberRightArrowImageView
                        .setOnClickListener(view -> mClickListener.onOrderItemClick(index));
                break;
            case DETAILS_VIEW_TYPE:
                OrderItemsViewholder orderItemsViewholder = (OrderItemsViewholder) viewHolder;
                GetOrdersResponse.Order.Invoice invoice = item.getInvoice();
                orderItemsViewholder.setup(invoice, mOrderTrackingClickListener);

                if (invoice.getActions().size() == 0) {
                    orderItemsViewholder.orderOptionsLayout.setVisibility(View.GONE);
                } else {

                    if (invoice.getActions().contains(ActionConstants.ORDER_ACTION_CHECK_STATUS) ||
                            invoice.getActions().contains(ActionConstants.ORDER_ACTION_CHANGE_ADDRESS) ||
                            invoice.getActions().contains(ActionConstants.ORDER_ACTION_REFUND)) {

                        ((OrderItemsViewholder) viewHolder).orderOptionsLayout.setVisibility(View.VISIBLE);
                    }

                    ((OrderItemsViewholder) viewHolder).orderOptionsLayout.setOnClickListener(v ->
                            mClickListener.onOrderItemShowOptions(
                                    ((OrderItemsViewholder) viewHolder).orderOptionsLayout,
                                    item.getOrderNumber(),
                                    invoice.getId(), invoice.getNumber(), invoice.getActions()));
                }

                viewHolder.itemView.setOnClickListener(view -> mClickListener.onOrderItemClick(index));
                break;
            case SUBTITLE_VIEW_TYPE:
                ((SubtitleViewHolder) viewHolder).subtitle.setText(item.getTitle());
            default:
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class SubtitleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_subtitle)
        TextView subtitle;

        SubtitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OrdersViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_number_text_value)
        TextView orderNumberTextView;

        @BindView(R.id.order_number_right_arrow)
        ImageView orderNumberRightArrowImageView;

        OrdersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OrderItemsViewholder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_image_recyclerview)
        RecyclerView orderImagesRecyclerView;
        @BindView(R.id.orders_options)
        View orderOptionsLayout;

        @BindView(R.id.component_order_tracking)
        OrderTrackingView componentOrderTracking;

        OrderItemsViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        void setup(GetOrdersResponse.Order.Invoice invoice, OrderTrackingClickListener onClickListener) {

            Context context = itemView.getContext();
            String link = invoice.getDelivery().getTrackingUrl();

            orderImagesRecyclerView.setAdapter(new OrderImageAdapter(invoice.getOrdered()));
            orderImagesRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            orderImagesRecyclerView.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));

            componentOrderTracking.setup(invoice.getId(), invoice.getNumber(), invoice.getDelivery(), invoice.getDelivery().getTrackingUrl(), onClickListener);
        }
    }

    private static class SpacerViewHolder extends RecyclerView.ViewHolder {
        SpacerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class Item {
        enum Type {
            HEADER, INVOICE, SUBTITLE, SEPARATOR
        }

        private Type type;
        private String title = null;
        private GetOrdersResponse.Order.Invoice invoice = null;
        private String locationFilter = null;
        private int orderNumber = 0;

        private int index = -1;

        Item(int index, int orderNumber, GetOrdersResponse.Order.Invoice invoice, String locationFilter) {
            this.index = index;
            type = Type.INVOICE;
            this.orderNumber = orderNumber;
            this.invoice = invoice;
            this.locationFilter = locationFilter;
        }

        Item(int index, int orderNumber) {
            this.index = index;
            type = Type.HEADER;
            this.orderNumber = orderNumber;
        }

        Item(String title) {
            type = Type.SUBTITLE;
            this.title = title;
        }

        Item() {
            type = Type.SEPARATOR;
        }

        public Type getType() {
            return type;
        }

        public GetOrdersResponse.Order.Invoice getInvoice() {
            return invoice;
        }

        public int getOrderNumber() {
            return orderNumber;
        }

        public String getLocationFilter() {
            return locationFilter;
        }

        public String getTitle() {
            return title;
        }

        public int getIndex() {
            return index;
        }

        public boolean hasIndex() {
            return index >= 0;
        }
    }

    public static class ItemCounts {
        private int oldCount;
        private int newCount;

        public ItemCounts(int oldCount, int newCount) {
            this.oldCount = oldCount;
            this.newCount = newCount;
        }

        public int getOldCount() {
            return oldCount;
        }

        public int getNewCount() {
            return newCount;
        }
    }
}