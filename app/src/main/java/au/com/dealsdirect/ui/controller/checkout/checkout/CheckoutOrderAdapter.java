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
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.cart.CartDetailsMapper.MappedShipment;
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
    private final static int VIEW_TYPE_FOOTER = 1;
    private final static int VIEW_TYPE_SPACER = 2;
    private final static int VIEW_TYPE_LINE = 3;

    private Context mContext;
    private List<MappedShipment> mSourceData;
    private List<ItemData> mFlattenedData;
    private static final int MAX_ITEM_QTY = 5;
    private final CheckoutListener mClickListener;
    private ItemQuantityChangedListener itemQuantityChangedListener = null;

    private final boolean isShippingByPostcodeEnabled;
    private final String impossibleToDeliverAtLocationText;

    private boolean shouldAddSpacerOnTop = false;

    private EligibleProductsLinkListener eligibleProductsLinkListener = null;

    private String postcodeOverride = null;

    private Item itemDataData;

    final private CheckoutOrderPriceHelper checkoutOrderPriceHelper;

    public CheckoutOrderAdapter(Context context,
                                List<MappedShipment> data,
                                boolean isShippingByPostcodeEnabled,
                                String impossibleToDeliverAtLocationText,
                                CheckoutListener clickListener,
                                CheckoutOrderPriceInfoClickListener onClickItemPriceInfo) {
        this.mContext = context;
        this.mSourceData = data;
        this.isShippingByPostcodeEnabled = isShippingByPostcodeEnabled;
        this.impossibleToDeliverAtLocationText = impossibleToDeliverAtLocationText;
        this.mClickListener = clickListener;
        checkoutOrderPriceHelper = new CheckoutOrderPriceHelper(context, onClickItemPriceInfo);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
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
            case FOOTER:
                return VIEW_TYPE_FOOTER;
            case EMPTY_SPACE:
                return VIEW_TYPE_SPACER;
            case LINE:
                return VIEW_TYPE_LINE;
            case ITEM:
            default:
                return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemData itemData = mFlattenedData.get(position);

        switch (itemData.getType()) {
            case ITEM:
                itemDataData = itemData.getItem();
                setupViewHolderForItem((ItemViewHolder) holder, itemData.getItem());
                break;
            case FOOTER:
                setupViewHolderForTitle((FooterViewHolder) holder, itemData.getFooterTitle());
                break;
            case LINE:
            case EMPTY_SPACE:
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
        if (item.size == null || item.size.length() < 0) {
            holder.sizeText.setVisibility(View.INVISIBLE);
            holder.sizeValue.setVisibility(View.INVISIBLE);
        } else {
            holder.sizeText.setVisibility(View.VISIBLE);
            holder.sizeValue.setVisibility(View.VISIBLE);
            holder.sizeValue.setText(item.size);
        }
        holder.colorText.setVisibility(View.GONE);

        if (holder.personalisationLayout != null) {
            holder.personalisationLayout.inflateForCheckout(mContext, item.getCustomizableItemDetailsList());
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

    public void replaceData(List<MappedShipment> items) {
        replaceData(items, true);
    }

    public void replaceData(List<MappedShipment> items, boolean showFooter) {
        mSourceData = new ArrayList<>(items);
        flattenData(showFooter);
        notifyDataSetChanged();
    }

    private void flattenData(boolean showFooter) {
        mFlattenedData = new ArrayList<>();
        for (MappedShipment shipment : mSourceData) {
            mFlattenedData.add(new ItemData(ItemData.Type.EMPTY_SPACE));
            mFlattenedData.add(new ItemData(ItemData.Type.LINE));
            for (Item item : shipment.getMappedItems()) {
                mFlattenedData.add(new ItemData(item));
            }
            if (showFooter && shipment.getDeliveryPrice() != null) {
                mFlattenedData.add(new ItemData(
                        createTitleFromShippingFee(
                                shipment.getDeliveryPrice(),
                                shipment.getAmountToPromoPrice(),
                                shipment.getItems().get(0),
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

    private SpannableStringBuilder createTitleFromShippingFee(double fee,
                                                              double targetPriceForFreeShipping,
                                                              String item,
                                                              String estimateShipmentPostcode,
                                                              boolean shippingAvailability) {
        int start = 0;
        int color = mContext.getResources().getColor(R.color.checkout_item_footer_other_text_color);
        int redColor = mContext.getResources().getColor(R.color.checkout_item_footer_red_text_color);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(" ");

        if (fee > 0.0 || !shippingAvailability) {

            spannableStringBuilder.append(mContext.getResources().getString(R.string.shipping_text));

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
                final String unavailableText = impossibleToDeliverAtLocationText == null ? "Unavailable" : impossibleToDeliverAtLocationText;
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

                    String targetPriceString = mContext.getResources().getString(R.string.promo_shipping_text);
                    String replacementString = PriceUtils.getPriceStringValue(targetPriceForFreeShipping);
                    targetPriceString = targetPriceString.replace(
                            mContext.getResources().getString(R.string.promo_shipping_text_placeholder),
                            replacementString);
                    spannableStringBuilder.append(
                            targetPriceString);

                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            if (Objects.equals(item, itemDataData.id)) {
                                onEligibleProductsTapped(itemDataData.brandName);
                            }

                        }
                    };

                    String eligibleProductsString = mContext.getResources().getString(R.string.promo_shipping_eligible_products);
                    int eligibleProductsStart = targetPriceString.indexOf(eligibleProductsString) + start;

                    spannableStringBuilder.setSpan(
                            clickableSpan,
                            eligibleProductsStart,
                            eligibleProductsStart + eligibleProductsString.length(),
                            SPAN_EXCLUSIVE_INCLUSIVE);

                    int imagePosition = spannableStringBuilder.length();
                    String freeShippingString = "   " +
                            mContext.getResources().getString(R.string.free_shipping_text).toUpperCase();
                    spannableStringBuilder.append(
                            freeShippingString,
                            new StyleSpan(BOLD),
                            SPAN_EXCLUSIVE_INCLUSIVE);

                    spannableStringBuilder.setSpan(
                            new ForegroundColorSpan(color),
                            start,
                            spannableStringBuilder.length(),
                            SPAN_EXCLUSIVE_INCLUSIVE);

                    Drawable d = ContextCompat.getDrawable(mContext, R.drawable.ic_free_shipping);
                    if (d != null) {
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                        ImageSpan imageSpan = new ImageSpan(d, DynamicDrawableSpan.ALIGN_BASELINE);
                        spannableStringBuilder.setSpan(imageSpan, imagePosition + 1, imagePosition + 2, SPAN_INCLUSIVE_EXCLUSIVE);
                    }

                    StringUtils.applySpanToSubstringsMatching(
                            spannableStringBuilder,
                            new StyleSpan(BOLD),
                            mContext.getResources().getString(R.string.regex_currency),
                            SPAN_EXCLUSIVE_INCLUSIVE);
                }
            }

        } else {
            int imagePosition = spannableStringBuilder.length();
            String freeShippingString = "   " +
                    mContext.getResources().getString(R.string.free_shipping_text).toUpperCase();
            spannableStringBuilder.append(
                    freeShippingString,
                    new StyleSpan(BOLD),
                    SPAN_EXCLUSIVE_INCLUSIVE);

            spannableStringBuilder.setSpan(
                    new ForegroundColorSpan(color),
                    start,
                    spannableStringBuilder.length(),
                    SPAN_EXCLUSIVE_INCLUSIVE);

            Drawable d = ContextCompat.getDrawable(mContext, R.drawable.ic_free_shipping);
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

    @Override
    public int getItemCount() {
        return mFlattenedData == null ? 0 : mFlattenedData.size();
    }

    public class LineViewHolder extends RecyclerView.ViewHolder {
        public LineViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class SpacerViewHolder extends RecyclerView.ViewHolder {
        public SpacerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.partial_checkout_item_footer_title)
        TextView titleTextView;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            titleTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

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

        private Type type;
        private Item item = null;
        private SpannableStringBuilder footerTitle = null;

        ItemData(Type type) {
            this.type = type;
        }

        ItemData(Item item) {
            type = Type.ITEM;
            this.item = item;
        }

        ItemData(SpannableStringBuilder footerTitle) {
            type = Type.FOOTER;
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

    public void setItemQuantityChangedListener(ItemQuantityChangedListener itemQuantityChangedListener) {
        this.itemQuantityChangedListener = itemQuantityChangedListener;
    }
}
