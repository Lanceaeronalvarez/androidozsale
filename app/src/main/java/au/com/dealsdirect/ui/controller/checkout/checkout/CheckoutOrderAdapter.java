package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper.MappedShipment;
import au.com.dealsdirect.ui.custom.PersonalisationLayout;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;

/**
 * Created by smartwave on 28/06/2017.
 */

public class CheckoutOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int VIEW_TYPE_ITEM = 0;
    private final static int VIEW_TYPE_FOOTER = 1;
    private final static int VIEW_TYPE_SPACER = 2;

    private Context mContext;
    private List<MappedShipment> mSourceData;
    private List<ItemData> mFlattenedData;
    private CheckoutMvpPresenter<CheckoutMvpView> mPresenter;
    private int resLayout;
    private static final int MAX_ITEM_QTY = 5;
    private CheckoutListener mClickListener;

    private boolean shouldAddSpacerOnTop = false;

    public CheckoutOrderAdapter(Context context, List<MappedShipment> data, CheckoutMvpPresenter<CheckoutMvpView> presenter,
                                CheckoutListener clickListener) {
        this.mContext = context;
        this.mSourceData = data;
        this.mPresenter = presenter;
        this.mClickListener = clickListener;
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
                setupViewHolderForItem((ItemViewHolder) holder, itemData.getItem());
                break;
            case FOOTER:
                setupViewHolderForTitle((FooterViewHolder) holder, itemData.getFooterTitle());
                break;
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
                ImageUtils.loadImageDontAnimate(LegacyStringImageUtils.generateImageUrl(item.brandID, item.imageID, item.fileName), holder.image);
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

        holder.price.setText(PriceUtils.getPriceStringValue(item.price));
        holder.quantityLayout.setMax(MAX_ITEM_QTY);
        holder.quantityLayout.setQuantity(item.qty);
        holder.quantityLayout.setAutoUpdateQuantity(false);
        holder.quantityLayout.setEditTextToNonEditable();

        int subTotalVisibility = item.qty > 1 ? View.VISIBLE : View.GONE;
        holder.subTotal.setVisibility(subTotalVisibility);
        if (holder.subTotalLabel != null) {
            holder.subTotalLabel.setVisibility(subTotalVisibility);
        }
        if (item.qty > 1) {
            holder.subTotal.setText(PriceUtils.getPriceStringValue(item.getSubtotal()));
        }

        holder.quantityLayout.setOnQuantityChangeListener(new ProductQuantityLayout.onQuantityChangeListener() {
            @Override
            public void onQuantityIncrease(ProductQuantityLayout view, int value) {
                mPresenter.fetchAdjustItemQuantity("IncreaseOrderItem", item.id, view);
            }

            @Override
            public void onQuantityDecrease(ProductQuantityLayout view, int value) {
                mPresenter.fetchAdjustItemQuantity("DecreaseOrderItem", item.id, view);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            mClickListener.showItemDetail(holder, 0, "",
                    LegacyStringImageUtils.generateImageUrl(item.brandID, item.imageID, item.fileName),
                    "", item.getSaleID(), false, item.getItem(), item.getItem(),
                    String.valueOf(item.getPrice()), String.valueOf(item.getPrice()), item.getItemID());
        });
    }

    private void setupViewHolderForTitle(FooterViewHolder holder, SpannableStringBuilder title) {
        holder.titleTextView.setText(title);
    }

    public void replaceData(List<MappedShipment> items) {
        mSourceData = new ArrayList<>(items);
        flattenData();
        notifyDataSetChanged();
    }

    private void flattenData() {
        mFlattenedData = new ArrayList<>();
        for (MappedShipment shipment : mSourceData) {
            mFlattenedData.add(new ItemData());
            for (Item item : shipment.getMappedItems()) {
                mFlattenedData.add(new ItemData(item));
            }
            mFlattenedData.add(new ItemData(
                    createTitleFromShippingFee(
                            shipment.getDeliveryPrice(),
                            shipment.getAmountToPromoPrice()
                    )));
        }
        if (!shouldAddSpacerOnTop) {
            mFlattenedData.remove(0);
        }
    }

    private SpannableStringBuilder createTitleFromShippingFee(double fee,
                                                              double targetPriceForFreeShipping) {
        // TODO: formatting
        String shippingString = mContext.getResources().getString(R.string.shipping_with_colon) +
                PriceUtils.getPriceStringValue(fee);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(shippingString);
        StringUtils.applySpanToSubstringsMatching(
                spannableStringBuilder,
                new StyleSpan(BOLD),
                ".{1,}:",
                SPAN_EXCLUSIVE_INCLUSIVE);

        if (targetPriceForFreeShipping > 0) {
            int color = mContext.getResources().getColor(R.color.color_free_shipping_text);

            String targetPriceString = "\n" +
                    mContext.getResources().getString(R.string.promo_shipping_text);
            targetPriceString = targetPriceString.replace(
                    mContext.getResources().getString(R.string.promo_shipping_text_placeholder),
                    PriceUtils.getPriceStringValue(targetPriceForFreeShipping));
            spannableStringBuilder.append(
                    targetPriceString,
                    new ForegroundColorSpan(color),
                    SPAN_EXCLUSIVE_INCLUSIVE);

            int start = spannableStringBuilder.length();
            String freeShippingString = "\n  " +
                    mContext.getResources().getString(R.string.free_shipping_text).toUpperCase();
            spannableStringBuilder.append(
                    freeShippingString,
                    new ForegroundColorSpan(color),
                    SPAN_EXCLUSIVE_INCLUSIVE);
            spannableStringBuilder.setSpan(
                    new StyleSpan(BOLD),
                    start,
                    spannableStringBuilder.length(),
                    SPAN_EXCLUSIVE_INCLUSIVE);

            Drawable d = ContextCompat.getDrawable(mContext, R.drawable.ic_free_shipping);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(d, DynamicDrawableSpan.ALIGN_BASELINE);
            spannableStringBuilder.setSpan(imageSpan, start + 1, start + 2, SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannableStringBuilder;
    }

    @Override
    public int getItemCount() {
        return mFlattenedData == null ? 0 : mFlattenedData.size();
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
        private enum Type {
            ITEM, FOOTER, EMPTY_SPACE
        }

        private Type type;
        private Item item = null;
        private SpannableStringBuilder footerTitle = null;

        ItemData() {
            type = Type.EMPTY_SPACE;
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
}
