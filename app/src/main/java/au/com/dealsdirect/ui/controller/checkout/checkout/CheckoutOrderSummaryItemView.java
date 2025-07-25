package au.com.dealsdirect.ui.controller.checkout.checkout;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Shipment;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckoutOrderSummaryItemView extends RecyclerView.ViewHolder {

    @BindView(R.id.partial_checkout_summary_container)
    ViewGroup mSummaryLayout;
    @BindView(R.id.partial_checkout_summary_total)
    TextView mSummaryTotalTextView;
    @BindView(R.id.partial_checkout_summary_subtotal)
    TextView mSummarySubtotalTextView;
    @BindView(R.id.partial_checkout_summary_voucher)
    TextView mSummaryVoucherTextView;
    @BindView(R.id.partial_checkout_summary_shipping_label)
    TextView mSummaryShippingLabelTextView;
    @BindView(R.id.partial_checkout_summary_shipping_fee)
    TextView mSummaryShippingFeeTextView;
    @BindView(R.id.partial_checkout_summary_shipping_fee_container)
    ViewGroup mSummaryShippingFeeContainer;
    @BindView(R.id.partial_checkout_summary_tax)
    TextView mSummaryTaxTextView;
    @BindView(R.id.partial_checkout_summary_tax_container)
    ViewGroup mSummaryTaxContainer;
    @BindView(R.id.partial_checkout_summary_shipping_with_icon)
    RelativeLayout mFreeShippingLayout;
    @BindView(R.id.partial_checkout_summary_voucher_container)
    ViewGroup mVoucherValueContainer;
    @BindView(R.id.partial_checkout_voucher_value_text_view)
    TextView mVoucherValueTextView;

    @BindView(R.id.partial_checkout_voucher_container_layout)
    ViewGroup mVoucherContainerLayout;

    View.OnClickListener onAddVoucherClickListener = null;

    public CheckoutOrderSummaryItemView(ViewGroup parent) {
        this(LayoutInflater
                .from(parent.getContext()).inflate(R.layout.partial_checkout_item_summary,
                        parent,
                        false));
    }

    public CheckoutOrderSummaryItemView(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        mVoucherContainerLayout.setOnClickListener(onAddVoucherClickListener);
    }

    public void setOnAddVoucherClickListener(View.OnClickListener onClickListener) {
        onAddVoucherClickListener = onClickListener;
        if (mVoucherContainerLayout != null) {
            mVoucherContainerLayout.setOnClickListener(onClickListener);
        }
    }

    public void setupSummaryShipping(CartDetailsMapper cart, boolean isShippingByPostcodeEnabled, String unavailableText) {
        if (cart == null || cart.getSummary() == null) {
            mSummaryLayout.setVisibility(View.GONE);
            return;
        }
        mSummaryLayout.setVisibility(View.VISIBLE);
        Context context = itemView.getContext();

        final Summary summary = cart.getSummary();
        final DeliveryAddress deliveryAddress = cart.getDeliveryAddress();
        final boolean isShipmentAvailable = isShipmentAvailable(cart.getShipments());
        final boolean isAddressValid = deliveryAddress != null;

        if (deliveryAddress == null) {
            mSummaryTotalTextView.setVisibility(View.GONE);
        } else {
            mSummaryTotalTextView.setVisibility(View.VISIBLE);
            mSummaryTotalTextView.setText(PriceUtils.getPriceStringValue(summary.getTotal()));
        }

        mSummaryShippingFeeContainer.setVisibility(View.VISIBLE);

        mSummarySubtotalTextView.setText(PriceUtils.getPriceStringValue(summary.getSubtotal()));
        String postcode = deliveryAddress != null ? deliveryAddress.getPostcode() : null;

        if (!isShippingByPostcodeEnabled ||
                postcode == null || summary.getDelivery() == null) {
            mSummaryShippingLabelTextView.setText(context.getResources().getString(R.string.shipping_text));
        } else {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(
                    context.getResources().getString(R.string.shipping_text)
            );
            spannableStringBuilder.append(" (");
            int start = spannableStringBuilder.length();
            int color = context.getResources().getColor(R.color.checkout_item_footer_other_text_color);
            spannableStringBuilder.append(
                    postcode,
                    new StyleSpan(BOLD),
                    SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(
                    new ForegroundColorSpan(color),
                    start,
                    spannableStringBuilder.length(),
                    SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(")");
            mSummaryShippingLabelTextView.setText(spannableStringBuilder);
        }

        if (isShippingByPostcodeEnabled && !isShipmentAvailable) {
            mSummaryShippingFeeTextView.setVisibility(View.VISIBLE);
            mSummaryShippingFeeTextView.setText(unavailableText);
            mSummaryShippingFeeTextView.setTextColor(context.getResources().getColor(R.color.checkout_item_footer_red_text_color));
            mFreeShippingLayout.setVisibility(View.GONE);
        } else if (!isAddressValid) {
            mSummaryShippingFeeTextView.setVisibility(View.VISIBLE);
            mFreeShippingLayout.setVisibility(View.GONE);
            mSummaryShippingFeeTextView.setText(context.getResources().getString(R.string.enter_address_above));
            mSummaryShippingFeeTextView.setTextColor(context.getResources().getColor(R.color.enter_address_text_color));
        } else if (summary.getDelivery() == null) {
            mSummaryShippingFeeTextView.setVisibility(View.GONE);
            mFreeShippingLayout.setVisibility(View.GONE);
        } else if (summary.getDelivery() == 0) {
            mSummaryShippingFeeTextView.setVisibility(View.GONE);
            mFreeShippingLayout.setVisibility(View.VISIBLE);
        } else {
            mSummaryShippingFeeTextView.setVisibility(View.VISIBLE);
            mSummaryShippingFeeTextView.setText(PriceUtils.getPriceStringValue(summary.getDelivery()));
            mSummaryShippingFeeTextView.setTextColor(context.getResources().getColor(R.color.text_dark));
            mFreeShippingLayout.setVisibility(View.GONE);
        }
    }

    public void setupSummaryVouchers(CartDetailsMapper cart) {
        if (cart == null || cart.getSummary() == null) {
            return;
        }

        Context context = itemView.getContext();

        final Summary summary = cart.getSummary();

        mSummaryVoucherTextView.setText(PriceUtils.getPriceStringValue(summary.getDiscount()));

        if (summary.getTax() > 0) {
            mSummaryTaxTextView.setText(PriceUtils.getPriceStringValue(summary.getTax()));
            mSummaryTaxContainer.setVisibility(View.VISIBLE);
        } else {
            mSummaryTaxContainer.setVisibility(View.GONE);
        }

        if (summary.getDiscount() > 0) {
            mVoucherValueContainer.setVisibility(View.VISIBLE);
            mVoucherValueTextView.setVisibility(View.VISIBLE);
            final String voucherValueText = PriceUtils.getPriceStringValue(summary.getDiscount()) + " " + context.getResources().getString(R.string.voucher);
            mVoucherValueTextView.setText(voucherValueText);
        } else {
            mVoucherValueTextView.setVisibility(View.GONE);
            mVoucherValueContainer.setVisibility(View.GONE);
        }
    }

    private boolean isShipmentAvailable(List<Shipment> shipments) {
        if (shipments == null) {
            return false;
        }
        for (Shipment shipment : shipments) {
            if (!shipment.getShippingAvailability()) {
                return false;
            }
        }
        return true;
    }
}
