package au.com.dealsdirect.ui.controller.checkout.checkout;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.StringUtils;

public class CheckoutTitleFromShippingFeeHelper {

    private final Context context;
    private String postcodeOverride;
    private boolean isShippingByPostcodeEnabled;
    private String impossibleToDeliverAtLocationText;
    private OnEligibleProductsClickListener onEligibleProductsClickListener;

    public CheckoutTitleFromShippingFeeHelper(Context context,
                                              String postcodeOverride,
                                              boolean isShippingByPostcodeEnabled,
                                              String impossibleToDeliverAtLocationText,
                                              OnEligibleProductsClickListener onEligibleProductsClickListener) {
        this.context = context;
        this.postcodeOverride = postcodeOverride;
        this.isShippingByPostcodeEnabled = isShippingByPostcodeEnabled;
        this.impossibleToDeliverAtLocationText = impossibleToDeliverAtLocationText;
        this.onEligibleProductsClickListener = onEligibleProductsClickListener;
    }

    public String getPostcodeOverride() {
        return postcodeOverride;
    }

    public void setPostcodeOverride(String postcodeOverride) {
        this.postcodeOverride = postcodeOverride;
    }

    public boolean isShippingByPostcodeEnabled() {
        return isShippingByPostcodeEnabled;
    }

    public void setShippingByPostcodeEnabled(boolean shippingByPostcodeEnabled) {
        isShippingByPostcodeEnabled = shippingByPostcodeEnabled;
    }

    public String getImpossibleToDeliverAtLocationText() {
        return impossibleToDeliverAtLocationText;
    }

    public void setImpossibleToDeliverAtLocationText(String impossibleToDeliverAtLocationText) {
        this.impossibleToDeliverAtLocationText = impossibleToDeliverAtLocationText;
    }

    public void setOnEligibleProductsClickListener(OnEligibleProductsClickListener onEligibleProductsClickListener) {
        this.onEligibleProductsClickListener = onEligibleProductsClickListener;
    }

    private SpannableStringBuilder createTitleFromShippingFee(double fee,
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
                spannableStringBuilder.append(" \n ");
                spannableStringBuilder.append(
                        impossibleToDeliverAtLocationText,
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
                            if (onEligibleProductsClickListener != null) {
                                onEligibleProductsClickListener.onClick(item.brandName);
                            }
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

    public SpannableStringBuilder getTitle(CartDetailsMapper.MappedShipment shipment) {
        return createTitleFromShippingFee(
                shipment.getDeliveryPrice(),
                shipment.getAmountToPromoPrice(),
                shipment.getMappedItems().get(0),
                postcodeOverride != null ? postcodeOverride : shipment.getEstimateShipmentPostcode(),
                shipment.getShippingAvailability());
    }

    public interface OnEligibleProductsClickListener {
        void onClick(String locationFilterHash);
    }
}
