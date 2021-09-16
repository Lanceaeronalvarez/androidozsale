package au.com.dealsdirect.ui.controller.orders.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.utils.ActionConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.ImageUtils;

/**
 * Created by MTC on 2019-07-12.
 */
public class BottomDialogCancelOrders extends BottomSheetDialogFragment {

    private int invoiceNumber = 0;
    private boolean shouldShowCancelOrder = false;
    private String imageUrl = "";
    private int quantity = 0;
    private String itemDescription = "";
    private BottomDialogButtonListener listener;

    public BottomDialogCancelOrders(@NonNull BottomDialogButtonListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_order_content, container, false);

        if (getArguments() != null) {
            Bundle bundle = getArguments();

            if (bundle.containsKey(ActionConstants.ORDER_INVOICE_NUMBER)) {
                invoiceNumber = bundle.getInt(ActionConstants.ORDER_INVOICE_NUMBER);
            }

            if (bundle.containsKey(ActionConstants.ORDER_SHOULD_SHOW_CANCEL_ORDER)) {
                shouldShowCancelOrder = bundle.getBoolean(ActionConstants.ORDER_SHOULD_SHOW_CANCEL_ORDER);
            }

            if (bundle.containsKey(ActionConstants.ORDER_ITEM_DESCRIPTION)) {
                itemDescription = bundle.getString(ActionConstants.ORDER_ITEM_DESCRIPTION);
            }

            if (bundle.containsKey(ActionConstants.ORDER_ITEM_IMAGE_URL)) {
                imageUrl = bundle.getString(ActionConstants.ORDER_ITEM_IMAGE_URL);
            }

            if (bundle.containsKey(ActionConstants.ORDER_QUANTITY)) {
                quantity = bundle.getInt(ActionConstants.ORDER_QUANTITY);
            }
        }

        if (shouldShowCancelOrder) {
            RelativeLayout mCancelOrderLayout = v.findViewById(R.id.cancel_order_layout);
            mCancelOrderLayout.setVisibility(View.VISIBLE);

            Button mYesButton = v.findViewById(R.id.button_yes_cancel_order);
            Button mNoButton = v.findViewById(R.id.button_no_cancel_order);
            ImageButton mCloseButton = v.findViewById(R.id.img_order_button_close);
            TextView mTextOrderNumber = v.findViewById(R.id.cancel_order_number_text);

            String description = Integer.toString(invoiceNumber);
            mTextOrderNumber.setText(description);

            mYesButton.setOnClickListener(v1 -> {
                BottomDialogCancelOrders.this.dismiss();
                listener.onYes(null);
            });

            mNoButton.setOnClickListener(v12 -> {
                BottomDialogCancelOrders.this.dismiss();
                listener.onNo(null);
            });

            mCloseButton.setOnClickListener(v13 -> {
                BottomDialogCancelOrders.this.dismiss();
                listener.onClose(null);
            });

        } else {
            RelativeLayout mCancelItemOrderLayout = v.findViewById(R.id.cancel_item_order);
            mCancelItemOrderLayout.setVisibility(View.VISIBLE);

            Button mYesButton = v.findViewById(R.id.button_yes_cancel_item);
            Button mNoButton = v.findViewById(R.id.button_no_cancel_item);
            ImageButton mItemCloseButton = v.findViewById(R.id.img_button_close);
            ImageView mItemImage = v.findViewById(R.id.item_cancel_image);
            TextView mItemName = v.findViewById(R.id.item_name);
            ProductQuantityLayout mQuantity = v.findViewById(R.id.item_quantity);

            mItemName.setText(itemDescription);

            ImageUtils.loadImage(imageUrl, mItemImage);

            mQuantity.setQuantity(1);
            mQuantity.setAutoUpdateQuantity(false);
            mQuantity.setMin(1);
            mQuantity.setMax(quantity);
            mQuantity.setEditTextToNonEditable();

            mQuantity.setOnQuantityChangeListener(new ProductQuantityLayout.onQuantityChangeListener() {
                @Override
                public void onQuantityIncrease(ProductQuantityLayout view, int value) {
                    if (value < quantity) {
                        value++;
                    }
                    mQuantity.setQuantity(value);
                }

                @Override
                public void onQuantityDecrease(ProductQuantityLayout view, int value) {
                    if (value > 1) {
                        value--;
                    }
                    mQuantity.setQuantity(value);
                }
            });

            mYesButton.setOnClickListener(v14 -> {
                BottomDialogCancelOrders.this.dismiss();
                listener.onYes(mQuantity.getQuantity());
            });

            mNoButton.setOnClickListener(v15 -> {
                BottomDialogCancelOrders.this.dismiss();
                listener.onNo(null);
            });

            mItemCloseButton.setOnClickListener(v16 -> {
                AppLogger.d("close button clicked");
                BottomDialogCancelOrders.this.dismiss();
                listener.onClose(null);
            });
        }


        return v;
    }

    public void setListener(BottomDialogButtonListener listener) {
        this.listener = listener;
    }

    public interface BottomDialogButtonListener {
        void onYes(Object object);

        void onNo(Object object);

        void onClose(Object object);
    }
}
