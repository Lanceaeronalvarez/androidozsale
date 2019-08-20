package au.com.dealsdirect.ui.controller.orders.orders;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.ActionConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.ImageUtils;

/**
 * Created by MTC on 2019-07-12.
 */
public class BottomDialogCancelOrders extends BottomSheetDialogFragment {

    private String invoiceNumber = "";
    private String reason = "";
    private boolean shouldShowCancelOrder = false;
    private String imageUrl = "";
    private String quantity = "";
    private String totalItems = "";
    private String itemDescription = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_content, container, false);

        if (getArguments() != null) {
            Bundle bundle = getArguments();

            if (bundle.containsKey(ActionConstants.ORDER_INVOICE_NUMBER)) {
                invoiceNumber = bundle.getString(ActionConstants.ORDER_INVOICE_NUMBER);
            }

            if (bundle.containsKey(ActionConstants.ORDER_REASON)) {
                reason = bundle.getString(ActionConstants.ORDER_REASON);
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
                quantity = bundle.getString(ActionConstants.ORDER_QUANTITY);
            }

            if (bundle.containsKey(ActionConstants.ORDER_SUBTOTAL_ITEM)) {
                totalItems = bundle.getString(ActionConstants.ORDER_SUBTOTAL_ITEM);
            }
        }

        if (shouldShowCancelOrder) {
            RelativeLayout mCancelOrderLayout = (RelativeLayout) v.findViewById(R.id.cancel_order_layout);
            mCancelOrderLayout.setVisibility(View.VISIBLE);

            Button mYesButton = (Button) v.findViewById(R.id.button_yes_cancel_order);
            Button mNoButton = (Button) v.findViewById(R.id.button_no_cancel_order);
            ImageButton mCloseButton = (ImageButton) v.findViewById(R.id.img_order_button_close);
            TextView mTextOrderNumber = (TextView) v.findViewById(R.id.cancel_order_number_text);

            mTextOrderNumber.setText(invoiceNumber);

            mYesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.callRefundOrder(invoiceNumber, reason, new JSONObject());
                    BottomDialogCancelOrders.this.dismiss();
                }
            });

            mNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomDialogCancelOrders.this.dismiss();
                }
            });

            mCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomDialogCancelOrders.this.dismiss();
                }
            });

        } else {
            RelativeLayout mCancelItemOrderLayout = (RelativeLayout) v.findViewById(R.id.cancel_item_order);
            mCancelItemOrderLayout.setVisibility(View.VISIBLE);

            Button mYesButton = (Button) v.findViewById(R.id.button_yes_cancel_item);
            Button mNoButton = (Button) v.findViewById(R.id.button_no_cancel_item);
            ImageButton mItemCloseButton = (ImageButton) v.findViewById(R.id.img_button_close);
            ImageView mItemImage = (ImageView) v.findViewById(R.id.item_cancel_image);
            TextView mItemName = (TextView) v.findViewById(R.id.item_name);
            ProductQuantityLayout mQuantity = (ProductQuantityLayout) v.findViewById(R.id.item_quantity);

            mItemName.setText(itemDescription);

            ImageUtils.loadImage(imageUrl, mItemImage);

            mQuantity.setQuantity(Integer.parseInt(quantity));
            mQuantity.setAutoUpdateQuantity(false);
            mQuantity.setMax(Integer.parseInt(totalItems));
            mQuantity.setEditTextToNonEditable();

            mQuantity.setOnQuantityChangeListener(new ProductQuantityLayout.onQuantityChangeListener() {
                @Override
                public void onQuantityIncrease(ProductQuantityLayout view, int value) {
                    if (value < Integer.parseInt(totalItems)) {
                        value++;
                    }
                    mQuantity.setQuantity(value);
                }

                @Override
                public void onQuantityDecrease(ProductQuantityLayout view, int value) {
                    if (value != 1) {
                        value--;
                    }
                    mQuantity.setQuantity(value);
                }
            });

            mYesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(invoiceNumber, mQuantity.getQuantity());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    MainActivity.callRefundOrder(invoiceNumber, reason, jsonObject);
                    BottomDialogCancelOrders.this.dismiss();
                }
            });

            mNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomDialogCancelOrders.this.dismiss();
                }
            });

            mItemCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppLogger.d("close button clicked");
                    BottomDialogCancelOrders.this.dismiss();
                }
            });
        }


        return v;
    }

}
