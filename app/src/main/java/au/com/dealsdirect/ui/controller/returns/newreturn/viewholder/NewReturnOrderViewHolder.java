package au.com.dealsdirect.ui.controller.returns.newreturn.viewholder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnItem;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 7/24/17.
 */

public class NewReturnOrderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.new_return_request_item_name)
    public TextView newReturnItemNameTextView;
    @BindView(R.id.new_returns_order_item_size_value)
    public TextView newReturnItemSizeText;
    @BindView(R.id.new_return_item_price)
    public TextView newReturnItemPriceTextView;
    @BindView(R.id.new_returns_order_subtotal)
    public TextView newReturnItemSubTotalTextView;
    @BindView(R.id.new_return_detail_item_image)
    public ImageView newReturnItemImageView;
    @BindView(R.id.new_return_item_detail_checkbox)
    public CheckBox newReturnItemCheckBox;
    @BindView(R.id.new_return_order_quantity)
    public ProductQuantityLayout productQuantityLayout;
    @BindView(R.id.new_return_item_invoice)
    public TextView newReturnInvoiceText;

    public NewReturnOrderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setup(NewReturnItem item, int invoiceNumber) {
        newReturnItemSizeText.setText(item.getSize());
        newReturnItemPriceTextView.setText(PriceUtils.getPriceStringValue(item.getPrice()));
        newReturnItemSubTotalTextView.setText(PriceUtils.getPriceStringValue(item.getPriceTotal()));
        newReturnItemNameTextView.setText(item.getName());

        productQuantityLayout.setMax(item.getQuantity());
        productQuantityLayout.setQuantity(1);
        productQuantityLayout.setEditTextToNonEditable();

        final String invoiceNumberText = itemView.getContext().getResources().getString(R.string.invoice_text) + " " +
                invoiceNumber;
        newReturnInvoiceText.setText(invoiceNumberText);

        final String imageUrl = item.getImageUrl();

        ImageUtils.loadImageImmediate(imageUrl, newReturnItemImageView, null);
    }

    public void setupCheckBox(NewReturnItem item, boolean isChecked, ValueChangedListener listener) {
        newReturnItemCheckBox.setChecked(isChecked);

        newReturnItemCheckBox.setOnCheckedChangeListener((buttonView, isChecked2) -> {
            final int quantityVal = Integer.parseInt(productQuantityLayout.getQuantity());
            if (isChecked2 && quantityVal == 0) {
                productQuantityLayout.setQuantity(1);
            }
            final NewReturnItem clonedItem = new NewReturnItem(item);
            clonedItem.setQuantity(quantityVal);
            listener.updateReturnValue(clonedItem, isChecked2);
        });
    }

    public void setupQuantityLayout(NewReturnItem item, ValueChangedListener listener) {
        productQuantityLayout.setOnQuantityChangeListener(new ProductQuantityLayout.onQuantityChangeListener() {
            @Override
            public void onQuantityIncrease(ProductQuantityLayout view, int value) {
                final NewReturnItem clonedItem = new NewReturnItem(item);
                clonedItem.setQuantity(value);
                listener.updateReturnValue(clonedItem, isChecked());
                productQuantityLayout.resetLoaders();
            }

            @Override
            public void onQuantityDecrease(ProductQuantityLayout view, int value) {
                if (value <= 0) {
                    newReturnItemCheckBox.setChecked(false);
                }
                final NewReturnItem clonedItem = new NewReturnItem(item);
                clonedItem.setQuantity(value);
                listener.updateReturnValue(clonedItem, isChecked());
                productQuantityLayout.resetLoaders();
            }
        });
    }

    private boolean isChecked() {
        return newReturnItemCheckBox.isChecked();
    }

    public interface ValueChangedListener {
        void updateReturnValue(NewReturnItem item, boolean isChecked);
    }
}
