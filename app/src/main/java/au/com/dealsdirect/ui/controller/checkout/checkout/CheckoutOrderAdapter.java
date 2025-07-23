package au.com.dealsdirect.ui.controller.checkout.checkout;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.data.cart.CartDetailsMapper.MappedShipment;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.ui.custom.PersonalisationLayout;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckoutOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int VIEW_TYPE_ITEM = 0;
    private final static int VIEW_TYPE_ITEM_LARGE = 1;
    private final static int VIEW_TYPE_FOOTER = 2;
    private final static int VIEW_TYPE_SPACER = 3;
    private final static int VIEW_TYPE_LINE = 4;
    private final static int VIEW_TYPE_SUMMARY = 5;

    private CartDetailsMapper mSourceData = null;
    private List<ItemData> mFlattenedData;
    private static final int MAX_ITEM_QTY = 5;
    private final CheckoutListener mClickListener;
    private ItemQuantityChangedListener itemQuantityChangedListener = null;

    private final boolean isShippingByPostcodeEnabled;
    private final ImpossibleToDeliverAtLocationTextRetriever impossibleToDeliverAtLocationTextRetriever;
    private final UnavailableTextRetriever unavailableTextRetriever;

    private boolean shouldAddSpacerOnTop = false;

    private EligibleProductsLinkListener eligibleProductsLinkListener = null;

    private String postcodeOverride = null;

    final private CheckoutOrderPriceHelper checkoutOrderPriceHelper;

    private boolean willShowLargeImages; // TODO
    private boolean willShowItems = true;
    private boolean willShowSummary = false;
    private View.OnClickListener onAddVoucherClickListener = null;

    public CheckoutOrderAdapter(Context context,
                                boolean willShowLargeImages,
                                boolean isShippingByPostcodeEnabled,
                                ImpossibleToDeliverAtLocationTextRetriever impossibleToDeliverAtLocationTextRetriever,
                                UnavailableTextRetriever unavailableTextRetriever,
                                CheckoutListener clickListener,
                                CheckoutOrderPriceInfoClickListener onClickItemPriceInfo) {
        this.willShowLargeImages = willShowLargeImages;
        this.isShippingByPostcodeEnabled = isShippingByPostcodeEnabled;
        this.impossibleToDeliverAtLocationTextRetriever = impossibleToDeliverAtLocationTextRetriever;
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
        if (position == getItemCount() - 1 && willShowSummary) {
            return VIEW_TYPE_SUMMARY;
        }
        switch (mFlattenedData.get(position).getType()) {
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

    public void replaceData(Context context, CartDetailsMapper cart) {
        replaceData(context, cart, true);
    }

    public void replaceData(Context context, CartDetailsMapper cart, boolean showFooter) {
        mSourceData = cart;
        flattenData(context, showFooter);
        notifyDataSetChanged();
    }

    private void flattenData(Context context, boolean showFooter) {
        mFlattenedData = new ArrayList<>();
        final List<MappedShipment> shipments = new ArrayList<>();
        if (mSourceData != null) {
            shipments.addAll(mSourceData.getMappedShipments());
        }
        for (MappedShipment shipment : shipments) {
            mFlattenedData.add(new ItemData(ItemData.Type.EMPTY_SPACE));
            mFlattenedData.add(new ItemData(ItemData.Type.LINE));
            for (Item item : shipment.getMappedItems()) {
                mFlattenedData.add(new ItemData(item));
            }
            if (showFooter && shipment.getDeliveryPrice() != null) {
                mFlattenedData.add(new ItemData(
                        createTitleFromShippingFee(
                                context,
                                shipment.getDeliveryPrice(),
                                shipment.getAmountToPromoPrice(),
                                shipment.getMappedItems().get(0),
                                postcodeOverride != null ? postcodeOverride : shipment.getEstimateShipmentPostcode(),
                                shipment.getShippingAvailability()
                        )));
            }
            mFlattenedData.add(new ItemData(ItemData.Type.LINE));
        }
        if (!shouldAddSpacerOnTop && !mFlattenedData.isEmpty()) {
            mFlattenedData.remove(0);
        }
    }

    private SpannableStringBuilder createTitleFromShippingFee(Context context,
                                                              double fee,
                                                              double targetPriceForFreeShipping,
                                                              Item item,
                                                              String estimateShipmentPostcode,
                                                              boolean shippingAvailability) {
        int start = 0;
        int color = context.getResources().getColor(R.color.checkout_item_footer_other_text_color);
        int redColor = context.getResources().getColor(R.color.checkout_item_footer_red_text_color);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(" ");

        if (fee > 0.0 || !shippingAvailability) {

            spannableStringBuilder.append(context.getResources().getString(R.string.shipping_text));

            if (isShippingByPostcodeEnabled &&
                    estimateShipmentPostcode != null) {
                spannableStringBuilder.append(" (");
                start = spannableStringBuilder.length();
                spannableStringBuilder.append(
                        estimateShipmentPostcode,
                        new StyleSpan(BOLD),
                        SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(
                        new ForegroundColorSpan(color),
                        start,
                        spannableStringBuilder.length(),
                        SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append(")");
            }

            spannableStringBuilder.append(": ");

            if (isShippingByPostcodeEnabled && !shippingAvailability) {
                final String unavailableText;
                if (impossibleToDeliverAtLocationTextRetriever == null ||
                        impossibleToDeliverAtLocationTextRetriever.getString() == null) {
                    unavailableText = "Unavailable";
                } else {
                    unavailableText = impossibleToDeliverAtLocationTextRetriever.getString();
                }

                spannableStringBuilder.append(" \n ");
                spannableStringBuilder.append(
                        unavailableText,
                        new ForegroundColorSpan(redColor),
                        SPAN_EXCLUSIVE_INCLUSIVE);
            } else {
                spannableStringBuilder.append(
                        PriceUtils.getPriceStringValue(fee),
                        new StyleSpan(BOLD),
                        SPAN_EXCLUSIVE_INCLUSIVE);

                spannableStringBuilder.append(" ");

                if (targetPriceForFreeShipping > 0) {
                    start = spannableStringBuilder.length() + 1;

                    spannableStringBuilder.append("\n");

                    String targetPriceString = context.getResources().getString(R.string.promo_shipping_text);
                    String replacementString = PriceUtils.getPriceStringValue(targetPriceForFreeShipping);
                    targetPriceString = targetPriceString.replace(
                            context.getResources().getString(R.string.promo_shipping_text_placeholder),
                            replacementString);
                    spannableStringBuilder.append(
                            targetPriceString);

                    final ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                                onEligibleProductsTapped(item.brandName);
                        }
                    };

                    String eligibleProductsString = context.getResources().getString(R.string.promo_shipping_eligible_products);
                    int eligibleProductsStart = targetPriceString.indexOf(eligibleProductsString) + start;

                    spannableStringBuilder.setSpan(
                            clickableSpan,
                            eligibleProductsStart,
                            eligibleProductsStart + eligibleProductsString.length(),
                            SPAN_EXCLUSIVE_INCLUSIVE);

                    int imagePosition = spannableStringBuilder.length();
                    String freeShippingString = "   " +
                            context.getResources().getString(R.string.free_shipping_text).toUpperCase();
                    spannableStringBuilder.append(
                            freeShippingString,
                            new StyleSpan(BOLD),
                            SPAN_EXCLUSIVE_INCLUSIVE);

                    spannableStringBuilder.setSpan(
                            new ForegroundColorSpan(color),
                            start,
                            spannableStringBuilder.length(),
                            SPAN_EXCLUSIVE_INCLUSIVE);

                    Drawable d = ContextCompat.getDrawable(context, R.drawable.ic_free_shipping);
                    if (d != null) {
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                        ImageSpan imageSpan = new ImageSpan(d, DynamicDrawableSpan.ALIGN_BASELINE);
                        spannableStringBuilder.setSpan(imageSpan, imagePosition + 1, imagePosition + 2, SPAN_INCLUSIVE_EXCLUSIVE);
                    }

                    StringUtils.applySpanToSubstringsMatching(
                            spannableStringBuilder,
                            new StyleSpan(BOLD),
                            context.getResources().getString(R.string.regex_currency),
                            SPAN_EXCLUSIVE_INCLUSIVE);
                }
            }

        } else {
            int imagePosition = spannableStringBuilder.length();
            String freeShippingString = "   " +
                    context.getResources().getString(R.string.free_shipping_text).toUpperCase();
            spannableStringBuilder.append(
                    freeShippingString,
                    new StyleSpan(BOLD),
                    SPAN_EXCLUSIVE_INCLUSIVE);

            spannableStringBuilder.setSpan(
                    new ForegroundColorSpan(color),
                    start,
                    spannableStringBuilder.length(),
                    SPAN_EXCLUSIVE_INCLUSIVE);

            Drawable d = ContextCompat.getDrawable(context, R.drawable.ic_free_shipping);
            if (d != null) {
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                ImageSpan imageSpan = new ImageSpan(d, DynamicDrawableSpan.ALIGN_BASELINE);
                spannableStringBuilder.setSpan(imageSpan, imagePosition + 1, imagePosition + 2, SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }

        return spannableStringBuilder;
    }

    private void onEligibleProductsTapped(String locationFilterHash) {
        if (eligibleProductsLinkListener != null) {
            eligibleProductsLinkListener.onTapped(locationFilterHash);
        }
    }

    public void setEligibleProductsLinkListener(EligibleProductsLinkListener eligibleProductsLinkListener) {
        this.eligibleProductsLinkListener = eligibleProductsLinkListener;
    }

    public String getPostcodeOverride() {
        return postcodeOverride;
    }

    public void setPostcodeOverride(String postcodeOverride) {
        this.postcodeOverride = postcodeOverride;
    }

    public boolean willShowLargeImages() {
        return willShowLargeImages;
    }

    public void setWillShowLargeImages(boolean willShowLargeImages) {
        this.willShowLargeImages = willShowLargeImages;
        notifyDataSetChanged();
    }

    public void setWillShowSummary(boolean willShowSummary) {
        this.willShowSummary = willShowSummary;
        notifyDataSetChanged();
    }

    public void setWillShowItems(boolean willShowItems) {
        this.willShowItems = willShowItems;
        notifyDataSetChanged();
    }

    public void setOnAddVoucherClickListener(View.OnClickListener onAddVoucherClickListener) {
        this.onAddVoucherClickListener = onAddVoucherClickListener;
        if (willShowSummary) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    @Override
    public int getItemCount() {
        return (mFlattenedData == null || !willShowItems ? 0 : mFlattenedData.size()) + (willShowSummary ? 1 : 0);
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
            ITEM, FOOTER, EMPTY_SPACE, LINE
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

    public interface EligibleProductsLinkListener {
        void onTapped(String locationFilterHash);
    }

    public interface ItemQuantityChangedListener {
        void onIncrease(String itemId, int newCount, ProductQuantityLayout view);

        void onDecrease(String itemId, int newCount, ProductQuantityLayout view);
    }

    public interface ImpossibleToDeliverAtLocationTextRetriever {
        String getString();
    }

    public interface UnavailableTextRetriever {
        String getString();
    }

    public void setItemQuantityChangedListener(ItemQuantityChangedListener itemQuantityChangedListener) {
        this.itemQuantityChangedListener = itemQuantityChangedListener;
    }
}
