package au.com.dealsdirect.ui.controller.priceblock;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.ViewGroup;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.utils.PriceUtils;

public class SaleItemProductAlternatePriceBlockHelper extends SaleItemProductPriceBlockHelper{

    public SaleItemProductAlternatePriceBlockHelper(ViewGroup view) {
        super(view);
    }

    @Override
    public void setup(SaleItemProduct product, boolean isSupplierOriginalPriceInfoEnabled, boolean isMinimized) {
        final Context context = priceBlockViewGroup.getContext();
        final boolean isTablet = context.getResources().getBoolean(R.bool.is_tablet);

        //--------------------
        // PRICE VALUE SETUP
        //--------------------

        final Double middlePrice;
        final Double bottomPrice;

        if (product.getSalePrice() != null && product.getSalePrice().getValue() != 0) {
            middlePrice = product.getSalePrice().getValue();
        } else {
            middlePrice = product.getPrice() != null && product.getPrice().getValue() != 0 ? product.getPrice().getValue() : null;
        }

        if (middlePrice == null) {
            priceBlockViewGroup.setVisibility(View.GONE);
            return;
        } else {
            priceBlockViewGroup.setVisibility(View.VISIBLE);
        }

        bottomPrice = product.getOriginalPrice() != null && product.getOriginalPrice().getValue() != 0 ? product.getOriginalPrice().getValue() : null;

        //--------------------
        // TOP TEXT SETUP
        //--------------------

        topLeftTextView.setVisibility(View.INVISIBLE);
        topRightTextView.setVisibility(View.INVISIBLE);

        //--------------------
        // MIDDLE TEXT SETUP
        //--------------------

        final String middlePriceText = PriceUtils.getPriceStringValue(middlePrice);
        final SpannableStringBuilder middleLeftText = new SpannableStringBuilder();
        middleLeftText.append(middlePriceText);

        leftTextView.setText(middleLeftText);

        final String rightTextSeparator = !isTablet && isMinimized && context.getResources().getBoolean(R.bool.price_block_is_three_lines) ? "\n" : " ";
        final String savedMoneyValueString = product.getSavedMoneyValue() != null && product.getSavedMoneyValue() != 0 ? "SAVE" + rightTextSeparator + PriceUtils.getPriceStringValue(product.getSavedMoneyValue()) : null;
        final SpannableStringBuilder middleRightText = new SpannableStringBuilder();
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
}
