package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.data.cart.CartDetailsMapper.MappedShipment;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.ui.custom.PersonalisationLayout;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckoutOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int VIEW_TYPE_ITEM = 0;
    private final static int VIEW_TYPE_ITEM_LARGE = 1;
    private final static int VIEW_TYPE_FOOTER = 2;
    private final static int VIEW_TYPE_SPACER = 3;
    private final static int VIEW_TYPE_LINE = 4;
    private final static int VIEW_TYPE_SUMMARY = 5;

    private final AsyncListDiffer<ItemData> asyncListDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<ItemData>() {
        @Override
        public boolean areItemsTheSame(@NonNull ItemData oldItem, @NonNull ItemData newItem) {
            if (oldItem.getType().equals(newItem.getType())) {
                if (oldItem.getType().equals(ItemData.Type.ITEM)) {
                    return oldItem.getItem().equals(newItem.getItem());
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }

        @Override
        public boolean areContentsTheSame(@NonNull ItemData oldItem, @NonNull ItemData newItem) {
            if (oldItem.getType().equals(newItem.getType())) {
                if (oldItem.getType().equals(ItemData.Type.ITEM)) {
                    final String oldItemId = oldItem.getItem().getId();
                    final String newItemId = newItem.getItem().getId();
                    return (oldItemId == null && newItemId == null) ||
                            (oldItemId != null && oldItemId.equals(newItemId));
                } else {
                    return !oldItem.getType().equals(ItemData.Type.SUMMARY);
                }
            } else {
                return false;
            }
        }
    });
    private CartDetailsMapper mSourceData = null;
    private List<ItemData> mFlattenedData;
    private static final int MAX_ITEM_QTY = 5;
    private final CheckoutListener mClickListener;
    private ItemQuantityChangedListener itemQuantityChangedListener = null;

    private boolean isShippingByPostcodeEnabled;
    private final UnavailableTextRetriever unavailableTextRetriever;
    private final CheckoutTitleFromShippingFeeHelper titleFromShippingFeeHelper;
    private boolean shouldAddSpacerOnTop = false;

    final private CheckoutOrderPriceHelper checkoutOrderPriceHelper;

    private boolean willShowLargeImages; // TODO
    private View.OnClickListener onAddVoucherClickListener = null;

    public CheckoutOrderAdapter(Context context,
                                boolean willShowLargeImages,
                                boolean isShippingByPostcodeEnabled,
                                CheckoutTitleFromShippingFeeHelper titleFromShippingFeeHelper,
                                UnavailableTextRetriever unavailableTextRetriever,
                                CheckoutListener clickListener,
                                CheckoutOrderPriceInfoClickListener onClickItemPriceInfo) {
        this.willShowLargeImages = willShowLargeImages;
        this.isShippingByPostcodeEnabled = isShippingByPostcodeEnabled;
        this.titleFromShippingFeeHelper = titleFromShippingFeeHelper;
        this.unavailableTextRetriever = unavailableTextRetriever;
        this.mClickListener = clickListener;
        checkoutOrderPriceHelper = new CheckoutOrderPriceHelper(context, onClickItemPriceInfo);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_SUMMARY:
                return new CheckoutOrderSummaryItemView(parent);
            case VIEW_TYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.partial_checkout_item_footer,
                                parent,
                                false));
            case VIEW_TYPE_SPACER:
                return new SpacerViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.partial_checkout_item_spacer,
                                parent,
                                false));
            case VIEW_TYPE_LINE:
                return new LineViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.partial_checkout_item_line,
                                parent,
                                false));
            case VIEW_TYPE_ITEM_LARGE:
                return new ItemViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.partial_checkout_item_large,
                                parent,
                                false));
            case VIEW_TYPE_ITEM:
            default:
                return new ItemViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.partial_checkout_item,
                                parent,
                                false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (mFlattenedData.get(position).getType()) {
            case SUMMARY:
                return VIEW_TYPE_SUMMARY;
            case FOOTER:
                return VIEW_TYPE_FOOTER;
            case EMPTY_SPACE:
                return VIEW_TYPE_SPACER;
            case LINE:
                return VIEW_TYPE_LINE;
            case ITEM:
            default:
                return willShowLargeImages ? VIEW_TYPE_ITEM_LARGE : VIEW_TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_ITEM:
            case VIEW_TYPE_ITEM_LARGE:
                setupViewHolderForItem((ItemViewHolder) holder, mFlattenedData.get(position).getItem());
                break;
            case VIEW_TYPE_FOOTER:
                setupViewHolderForTitle((FooterViewHolder) holder, mFlattenedData.get(position).getFooterTitle());
                break;
            case VIEW_TYPE_SUMMARY:
                if (holder instanceof CheckoutOrderSummaryItemView) {
                    final CheckoutOrderSummaryItemView summaryItemView = (CheckoutOrderSummaryItemView) holder;
                    summaryItemView.setOnAddVoucherClickListener(onAddVoucherClickListener);
                    String unavailableText;
                    if (unavailableTextRetriever == null ||
                            unavailableTextRetriever.getString() == null) {
                        unavailableText = "Unavailable";
                    } else {
                        unavailableText = unavailableTextRetriever.getString();
                    }
                    summaryItemView.setupSummaryShipping(mSourceData, isShippingByPostcodeEnabled, unavailableText);
                    summaryItemView.setupSummaryVouchers(mSourceData);
                }
                break;
            case VIEW_TYPE_LINE:
            case VIEW_TYPE_SPACER:
            default:
                break;
        }
    }

    public boolean isShouldAddSpacerOnTop() {
        return shouldAddSpacerOnTop;
    }

    public void setShouldAddSpacerOnTop(boolean shouldAddSpacerOnTop) {
        this.shouldAddSpacerOnTop = shouldAddSpacerOnTop;
    }

    private void setupViewHolderForItem(ItemViewHolder holder, Item item) {
        final Context context = holder.itemView.getContext();
        //   (1) fix when item.fileName is null
        if (item.fileName != null) {
            if (!item.fileName.isEmpty()) {
                String newImageFilename = LegacyStringImageUtils.generateImageUrl(item.brandID, item.imageID, item.fileName);
                if (!holder.imageFilename.equals(newImageFilename)) {
                    holder.imageFilename = newImageFilename;
                    // rather display nothing than display the wrong image
                    holder.image.setImageDrawable(null);
                }
                ImageUtils.loadImageDontAnimate(holder.imageFilename, holder.image);
            }
        }

        holder.name.setText(item.item);
        if (item.size == null || item.size.isEmpty()) {
            holder.sizeText.setVisibility(View.INVISIBLE);
            holder.sizeValue.setVisibility(View.INVISIBLE);
        } else {
            holder.sizeText.setVisibility(View.VISIBLE);
            holder.sizeValue.setVisibility(View.VISIBLE);
            holder.sizeValue.setText(item.size);
        }
        holder.colorText.setVisibility(View.GONE);

        if (holder.personalisationLayout != null) {
            holder.personalisationLayout.inflateForCheckout(context, item.getCustomizableItemDetailsList());
        }

        checkoutOrderPriceHelper.setupPriceTextView(holder.price, item);

        holder.quantityLayout.setMax(MAX_ITEM_QTY);
        holder.quantityLayout.setQuantity(item.qty);
        holder.quantityLayout.setAutoUpdateQuantity(false);
        holder.quantityLayout.setEditTextToNonEditable();

        holder.subTotal.setVisibility(View.GONE);
        if (holder.subTotalLabel != null) {
            holder.subTotalLabel.setVisibility(View.GONE);
        }

        final String itemId = item.id;
        holder.quantityLayout.setOnQuantityChangeListener(new ProductQuantityLayout.onQuantityChangeListener() {
            @Override
            public void onQuantityIncrease(ProductQuantityLayout view, int value) {
                if (itemQuantityChangedListener != null) {
                    itemQuantityChangedListener.onIncrease(itemId, value, view);
                }
            }

            @Override
            public void onQuantityDecrease(ProductQuantityLayout view, int value) {
                if (itemQuantityChangedListener != null) {
                    itemQuantityChangedListener.onDecrease(itemId, value, view);
                }
            }
        });

        holder.itemView.setOnClickListener(v -> {
            mClickListener.showItemDetail(holder.image, 0, "",
                    LegacyStringImageUtils.generateImageUrl(item.brandID, item.imageID, item.fileName),
                    "", item.getSaleID(), false, item.getItem(), item.getItem(),
                    String.valueOf(item.getPrice()), String.valueOf(item.getPrice()), item.getItemID());
        });
    }

    private void setupViewHolderForTitle(FooterViewHolder holder, SpannableStringBuilder title) {
        holder.titleTextView.setText(title);
    }

    public void replaceData(CartDetailsMapper cart, boolean showItems, boolean showSummary, boolean showFooter) {
        mSourceData = cart;
        mFlattenedData = flattenData(showItems, showSummary, showFooter);
        asyncListDiffer.submitList(mFlattenedData);
    }

    private List<ItemData> flattenData(boolean showItems, boolean showSummary, boolean showFooter) {
        final List<ItemData> flattenedData = new ArrayList<>();
        if (showItems) {
            final List<MappedShipment> shipments = new ArrayList<>();
            if (mSourceData != null) {
                shipments.addAll(mSourceData.getMappedShipments());
            }
            for (MappedShipment shipment : shipments) {
                flattenedData.add(new ItemData(ItemData.Type.EMPTY_SPACE));
                flattenedData.add(new ItemData(ItemData.Type.LINE));
                for (Item item : shipment.getMappedItems()) {
                    flattenedData.add(new ItemData(item));
                }
                if (showFooter && shipment.getDeliveryPrice() != null) {
                    flattenedData.add(new ItemData(titleFromShippingFeeHelper.getTitle(shipment)));
                }
                flattenedData.add(new ItemData(ItemData.Type.LINE));
            }
            if (!shouldAddSpacerOnTop && !flattenedData.isEmpty()) {
                flattenedData.remove(0);
            }
        }
        if (showSummary) {
            flattenedData.add(new ItemData(ItemData.Type.SUMMARY));
        }
        return flattenedData;
    }

    public boolean isShippingByPostcodeEnabled() {
        return isShippingByPostcodeEnabled;
    }

    public void setShippingByPostcodeEnabled(boolean shippingByPostcodeEnabled) {
        isShippingByPostcodeEnabled = shippingByPostcodeEnabled;
        asyncListDiffer.submitList(mFlattenedData);
    }

    public boolean willShowLargeImages() {
        return willShowLargeImages;
    }

    public void setWillShowLargeImages(boolean willShowLargeImages) {
        this.willShowLargeImages = willShowLargeImages;
        asyncListDiffer.submitList(mFlattenedData);
    }

    public void setOnAddVoucherClickListener(View.OnClickListener onAddVoucherClickListener) {
        this.onAddVoucherClickListener = onAddVoucherClickListener;
        asyncListDiffer.submitList(mFlattenedData);
    }

    @Override
    public int getItemCount() {
        return mFlattenedData.size();
    }

    public static class LineViewHolder extends RecyclerView.ViewHolder {
        public LineViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class SpacerViewHolder extends RecyclerView.ViewHolder {
        public SpacerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.partial_checkout_item_footer_title)
        TextView titleTextView;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            titleTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_checkout_image)
        ImageView image;
        @BindView(R.id.item_checkout_name)
        TextView name;
        @BindView(R.id.item_checkout_size_text)
        TextView sizeText;
        @BindView(R.id.item_checkout_size_value)
        TextView sizeValue;
        @BindView(R.id.item_checkout_color_text)
        TextView colorText;
        @BindView(R.id.item_checkout_color)
        TextView colorValue;
        @BindView(R.id.item_checkout_price)
        TextView price;
        @BindView(R.id.item_checkout_quantity)
        ProductQuantityLayout quantityLayout;
        @BindView(R.id.item_subtotal_price)
        TextView subTotal;
        @Nullable
        @BindView(R.id.item_checkout_subtotal_label)
        TextView subTotalLabel;
        @Nullable
        @BindView(R.id.item_checkout_personalisation_layout)
        PersonalisationLayout personalisationLayout;

        String imageFilename = "";

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class ItemData {
        enum Type {
            ITEM, FOOTER, EMPTY_SPACE, LINE, SUMMARY
        }

        private final Type type;
        private final Item item;
        private final SpannableStringBuilder footerTitle;

        ItemData(Type type) {
            this.type = type;
            this.item = null;
            this.footerTitle = null;
        }

        ItemData(Item item) {
            type = Type.ITEM;
            this.item = item;
            this.footerTitle = null;
        }

        ItemData(SpannableStringBuilder footerTitle) {
            type = Type.FOOTER;
            this.item = null;
            this.footerTitle = footerTitle;
        }

        Item getItem() {
            return item;
        }

        SpannableStringBuilder getFooterTitle() {
            return footerTitle;
        }

        Type getType() {
            return type;
        }
    }

    public interface ItemQuantityChangedListener {
        void onIncrease(String itemId, int newCount, ProductQuantityLayout view);

        void onDecrease(String itemId, int newCount, ProductQuantityLayout view);
    }

    public interface UnavailableTextRetriever {
        String getString();
    }

    public void setItemQuantityChangedListener(ItemQuantityChangedListener itemQuantityChangedListener) {
        this.itemQuantityChangedListener = itemQuantityChangedListener;
    }
}
