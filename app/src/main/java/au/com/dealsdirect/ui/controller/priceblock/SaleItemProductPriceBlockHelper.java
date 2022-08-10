package au.com.dealsdirect.ui.controller.priceblock;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitemdetails.SaleItemDetails;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.utils.PriceUtils;

public class SaleItemProductPriceBlockHelper {
    final String PRICE_INFO_IMAGE_PLACEHOLDER = "asdf ";
    final String PRICE_INFO_IMAGE_SPACER = " ";

    protected final ViewGroup priceBlockViewGroup;
    protected final ViewGroup topTextContainer;
    protected final TextView topLeftTextView;
    protected final TextView topRightTextView;
    protected final TextView leftTextView;
    protected final TextView rightTextView;
    protected final View priceBlockBorder;
    protected final ViewGroup bottomTextContainer;
    protected final TextView bottomTextView;
    protected final TextView freeDeliveryTextView;

    protected View.OnClickListener onPriceInfoClickListener = null;
    protected ImageSpan priceInfoImageSpan = null;

    public SaleItemProductPriceBlockHelper(ViewGroup view) {
        priceBlockViewGroup = view.findViewById(R.id.price_block);
        topTextContainer = view.findViewById(R.id.top_text_container);
        topLeftTextView = view.findViewById(R.id.top_left_text);
        topRightTextView = view.findViewById(R.id.top_right_text);
        leftTextView = view.findViewById(R.id.left_text);
        rightTextView = view.findViewById(R.id.right_text);
        priceBlockBorder = view.findViewById(R.id.price_block_border);
        bottomTextContainer = view.findViewById(R.id.bottom_text_container);
        bottomTextView = view.findViewById(R.id.bottom_text);
        freeDeliveryTextView = view.findViewById(R.id.free_delivery_text);
    }

    public ViewGroup getPriceBlockViewGroup() {
        return priceBlockViewGroup;
    }

    public void setup(SaleItemProduct product, boolean isSupplierOriginalPriceInfoEnabled) {
        setup(product, isSupplierOriginalPriceInfoEnabled, true);
    }

    public void setup(SaleItemProduct product, boolean isSupplierOriginalPriceInfoEnabled, boolean isMinimized) {
        final Context context = priceBlockViewGroup.getContext();
        final boolean isTablet = context.getResources().getBoolean(R.bool.is_tablet);

        //--------------------
        // PRICE VALUE SETUP
        //--------------------

        final Double topPrice;
        final Double middlePrice;
        final Double bottomPrice;

        if (product.getSalePrice() != null && product.getSalePrice().getValue() != 0) {
            topPrice = product.getPrice() != null ? product.getPrice().getValue() : null;
            middlePrice = product.getSalePrice().getValue();
        } else {
            topPrice = null;
            middlePrice = product.getPrice() != null && product.getPrice().getValue() != 0 ? product.getPrice().getValue() : null;
        }

        bottomPrice = product.getOriginalPrice() != null && product.getOriginalPrice().getValue() != 0 ? product.getOriginalPrice().getValue() : null;

        if (middlePrice == null) {
            priceBlockViewGroup.setVisibility(View.GONE);
            return;
        } else {
            priceBlockViewGroup.setVisibility(View.VISIBLE);
        }


        //--------------------
        // TOP TEXT SETUP
        //--------------------

        final String topPriceText = PriceUtils.getPriceStringValue(topPrice);
        final SpannableString topLeftText = topPrice != null ? new SpannableString("WAS " + topPriceText) : null;
        if (topLeftText != null) {
            topLeftText.setSpan(new StrikethroughSpan(), 4, topLeftText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        final String topRightText = product.getSalePercentOffText();

        topTextContainer.setVisibility(topLeftText == null && (topRightText == null || topRightText.isEmpty()) ? View.INVISIBLE : View.VISIBLE);
        topLeftTextView.setText(topLeftText);
        topRightTextView.setText(topRightText);
        topLeftTextView.setVisibility(topPrice == null || topPrice.equals(middlePrice) ? View.INVISIBLE : View.VISIBLE);
        topRightTextView.setVisibility(topRightText == null || topRightText.isEmpty() ? View.INVISIBLE : View.VISIBLE);


        //--------------------
        // MIDDLE TEXT SETUP
        //--------------------

        final String middlePriceText = PriceUtils.getPriceStringValue(middlePrice);
        final SpannableStringBuilder middleLeftText = new SpannableStringBuilder();
        if (shouldShowPriceRangeText(product)) {
            middleLeftText.append(product.getPriceRangeText() + "\n", new RelativeSizeSpan(0.6f), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        middleLeftText.append(middlePriceText);

        leftTextView.setText(middleLeftText);

        final String totalPercentOffString = product.getTotalPercentOff() != null && product.getTotalPercentOff() != 0 ? (int) Math.floor(product.getTotalPercentOff()) + "% OFF" : null;
        final String rightTextSeparator = !isTablet && isMinimized && context.getResources().getBoolean(R.bool.price_block_is_three_lines) ? "\n" : " ";
        final String savedMoneyValueString = product.getSavedMoneyValue() != null && product.getSavedMoneyValue() != 0 ? "SAVE" + rightTextSeparator + PriceUtils.getPriceStringValue(product.getSavedMoneyValue()) : null;
        final SpannableStringBuilder middleRightText = new SpannableStringBuilder();
        if (totalPercentOffString != null) {
            middleRightText.append(totalPercentOffString);
        }
        if (savedMoneyValueString != null) {
            if (middleRightText.length() > 0) {
                middleRightText.append("\n");
            }
            middleRightText.append(savedMoneyValueString);
        }

        if (middlePriceText.length() > 8) {
            middleRightText.setSpan(new RelativeSizeSpan(0.8f), 0, middleRightText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        rightTextView.setText(middleRightText);
        final boolean isRightPartVisible = middleRightText.length() > 0;
        priceBlockBorder.setVisibility(isRightPartVisible ? View.VISIBLE : View.GONE);


        //--------------------
        // BOTTOM TEXT SETUP
        //--------------------

        if (bottomPrice != null) {
            final String bottomTextPrefix = isSupplierOriginalPriceInfoEnabled ?
                    context.getResources().getString(R.string.price_block_bottom_text_alternate_prefix) :
                    context.getResources().getString(R.string.price_block_bottom_text_prefix);
            final boolean isBottomTextTwoLines = isMinimized && isSupplierOriginalPriceInfoEnabled && !isTablet;
            final String bottomPriceText = PriceUtils.getPriceStringValue(bottomPrice);
            final SpannableStringBuilder bottomText = new SpannableStringBuilder();

            bottomText.append(bottomTextPrefix);
            bottomText.append(isBottomTextTwoLines ? "\n" : " ");

            bottomText.append(bottomPriceText, new StrikethroughSpan(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            clearPriceInfoImageSpan();
            bottomTextView.setText(bottomText);
            setupPriceInfo();

            bottomTextContainer.getLayoutParams().height = (int) (isBottomTextTwoLines ?
                    context.getResources().getDimension(R.dimen.price_block_bottom_text_double_height) :
                    context.getResources().getDimension(R.dimen.price_block_bottom_text_single_height));
        } else {
            bottomTextView.setText(null);
        }
        bottomTextContainer.setVisibility(bottomPrice == null || bottomPrice.equals(middlePrice) ? View.INVISIBLE : View.VISIBLE);
    }

    public void setFreeDeliveryTextViewText(CharSequence text) {
        freeDeliveryTextView.setText(text);
        if (text != null && text.length() > 0) {
            freeDeliveryTextView.setVisibility(View.VISIBLE);
        } else {
            freeDeliveryTextView.setVisibility(View.GONE);
        }
    }

    public void setFreeDeliveryOnClickListener(View.OnClickListener onClickListener) {
        freeDeliveryTextView.setOnClickListener(onClickListener);
    }

    public void setPriceInfoOnClickListener(View.OnClickListener onClickListener) {
        setupPriceInfo(onClickListener);
        bottomTextContainer.setOnClickListener(onClickListener);
    }

    protected void setupPriceInfo() {
        setupPriceInfo(onPriceInfoClickListener);
    }

    private void setupPriceInfoImageSpan() {
        priceInfoImageSpan = new ImageSpan(bottomTextView.getContext(), R.drawable.ic_price_info, DynamicDrawableSpan.ALIGN_BOTTOM);
    }

    private boolean hasSetupPriceInfoImageSpan() {
        return priceInfoImageSpan != null;
    }

    protected void clearPriceInfoImageSpan() {
        priceInfoImageSpan = null;
    }

    private void setupPriceInfo(View.OnClickListener onClickListener) {
        final boolean shouldInsert = (onPriceInfoClickListener == null && onClickListener != null) ||
                (!hasSetupPriceInfoImageSpan() && onClickListener != null);
        final boolean shouldRemove = (onPriceInfoClickListener != null && onClickListener == null) ||
                (hasSetupPriceInfoImageSpan() && onClickListener == null);
        onPriceInfoClickListener = onClickListener;

        final SpannableStringBuilder bottomText = new SpannableStringBuilder(bottomTextView.getText());

        if (shouldInsert) {
            setupPriceInfoImageSpan();
            bottomText.append(PRICE_INFO_IMAGE_SPACER);
            bottomText.append(PRICE_INFO_IMAGE_PLACEHOLDER, priceInfoImageSpan, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            bottomTextView.setText(bottomText);
        }
        if (shouldRemove) {
            bottomText.removeSpan(priceInfoImageSpan);
            bottomText.delete(bottomText.length() - PRICE_INFO_IMAGE_PLACEHOLDER.length(), bottomText.length());
            bottomText.delete(bottomText.length() - PRICE_INFO_IMAGE_SPACER.length(), bottomText.length());
            clearPriceInfoImageSpan();
            bottomTextView.setText(bottomText);
        }
    }

    public static int getBottomTextViewHeight(Context context, boolean isMinimized, boolean isSupplierOriginalPriceInfoEnabled) {
        final boolean isBottomTextTwoLines = isMinimized && isSupplierOriginalPriceInfoEnabled && !context.getResources().getBoolean(R.bool.is_tablet);
        return (int) (isBottomTextTwoLines ?
                context.getResources().getDimension(R.dimen.price_block_bottom_text_double_height) :
                context.getResources().getDimension(R.dimen.price_block_bottom_text_single_height));
    }

    public static void setupSkeleton(ViewGroup skeletonPriceBlockView, boolean isMinimized, boolean isSupplierOriginalPriceInfoEnabled) {
        final Context context = skeletonPriceBlockView.getContext();
        final ViewGroup bottomTextContainer = skeletonPriceBlockView.findViewById(R.id.bottom_text_container);
        bottomTextContainer.getLayoutParams().height = getBottomTextViewHeight(context, isMinimized, isSupplierOriginalPriceInfoEnabled);
    }

    protected boolean shouldShowPriceRangeText(SaleItemProduct product) {
        final boolean hasPriceRangeText = product.getPriceRangeText() != null;
        if (product instanceof SaleItemDetails) {
            final List<SaleItemDetails> skuVariants = ((SaleItemDetails) product).getSkuVariants();
            return hasPriceRangeText && skuVariants != null && skuVariants.size() > 1;
        } else {
            return hasPriceRangeText;
        }
    }
}
