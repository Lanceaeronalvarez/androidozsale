package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.widget.TextView;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.utils.PriceUtils;

public class CheckoutOrderPriceHelper {
    final String PRICE_INFO_IMAGE_PLACEHOLDER = "asdf ";
    final String PRICE_INFO_IMAGE_SPACER = " ";
    private final Context context;
    private final CheckoutOrderPriceInfoClickListener onPriceInfoClicked;

    public CheckoutOrderPriceHelper(Context context,
                                    CheckoutOrderPriceInfoClickListener onPriceInfoClicked) {
        this.context = context;
        this.onPriceInfoClicked = onPriceInfoClicked;
    }

    public String getMultiBuyDescriptionFromItem(Item item) {
        if (item.multiBuyCount <= 0) {
            return null;
        }
        String priceText = PriceUtils.getPriceStringValue(item.multiBuyDiscountPrice / (double) item.multiBuyCount);
        String count = Integer.toString(item.multiBuyCount);
        return priceText + " for each item when you purchase " + count + " or more products in this sale";
    }

    private String getMultiBuyDiscountTextFromItem(Item item) {
        if (item.discount > 0) {
            return PriceUtils.getPriceStringValue(item.subtotalWithDiscount);
        } else {
            return PriceUtils.getPriceStringValue(item.multiBuyDiscountPrice / (double) item.multiBuyCount * item.qty);
        }
    }

    public void setupPriceTextView(TextView priceTextView, Item item) {
        String subtotalText = PriceUtils.getPriceStringValue(item.subtotal);
        boolean shouldDiscountBeDisplayed = item.subtotalWithDiscount > 0 && item.discountPercentOff > 0;
        if (item.multiBuyEnabled || shouldDiscountBeDisplayed) {
            boolean shouldShowPriceInfo = false;
            SpannableStringBuilder priceText = new SpannableStringBuilder();
            if (item.multiBuyEnabled) {
                priceText.append(getMultiBuyDiscountTextFromItem(item));
                shouldShowPriceInfo = true;
            } else if (shouldDiscountBeDisplayed) {
                priceText.append(PriceUtils.getPriceStringValue(item.subtotalWithDiscount));
            }
            final SpannableString wasPrice = new SpannableString(" WAS " + subtotalText);
            wasPrice.setSpan(new StrikethroughSpan(), 5, wasPrice.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            priceText.append(wasPrice);

            if (shouldShowPriceInfo && onPriceInfoClicked != null) {
                final ImageSpan priceInfoImageSpan = new ImageSpan(context, R.drawable.ic_price_info, DynamicDrawableSpan.ALIGN_BOTTOM);
                priceText.append(PRICE_INFO_IMAGE_SPACER);
                priceText.append(PRICE_INFO_IMAGE_PLACEHOLDER, priceInfoImageSpan, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                final String description = getMultiBuyDescriptionFromItem(item);
                priceTextView.setOnClickListener(v -> onPriceInfoClicked.onClick(description));
            } else {
                priceTextView.setOnClickListener(null);
            }

            priceTextView.setText(priceText);
        } else {
            priceTextView.setText(subtotalText);
        }
    }
}
