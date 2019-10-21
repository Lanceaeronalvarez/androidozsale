package au.com.dealsdirect.ui.controller.orders.orders;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.ActionConstants;

/**
 * Created by MTC on 2019-06-21.
 */
public class BottomSheetOrderDialog extends BottomSheetDialogFragment {

    private ArrayList<String> arrayList = new ArrayList<>();
    private String orderID = "";
    private String invoiceNumber;
    private String itemDescription = "";
    private String itemReturnID = "";
    private String productID = "";
    private boolean isItemCancel = false;
    private String imageUrl = "";
    private String reason = "";
    private String quantity = "";
    private String totalItems = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME,R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_order_content, container, false);

        if (getArguments() != null) {
            Bundle bundle = getArguments();

            if (bundle.containsKey(ActionConstants.ORDER_ARRAYS)) {
                arrayList = bundle.getStringArrayList(ActionConstants.ORDER_ARRAYS);
            }

            if (bundle.containsKey(ActionConstants.ORDER_ORDER_ID)) {
                orderID = bundle.getString(ActionConstants.ORDER_ORDER_ID);
            }

            if (bundle.containsKey(ActionConstants.ORDER_INVOICE_NUMBER)) {
                invoiceNumber = bundle.getString(ActionConstants.ORDER_INVOICE_NUMBER);
            }

            if (bundle.containsKey(ActionConstants.ORDER_ITEM_DESCRIPTION)) {
                itemDescription = bundle.getString(ActionConstants.ORDER_ITEM_DESCRIPTION);
            }

            if (bundle.containsKey(ActionConstants.ORDER_ITEM_RETURN_ID)) {
                itemReturnID = bundle.getString(ActionConstants.ORDER_ITEM_RETURN_ID);
            }

            if (bundle.containsKey(ActionConstants.ORDER_PRODUCT_ID)) {
                productID = bundle.getString(ActionConstants.ORDER_PRODUCT_ID);
            }

            if (bundle.containsKey(ActionConstants.ORDER_ITEM_IMAGE_URL)) {
                imageUrl = bundle.getString(ActionConstants.ORDER_ITEM_IMAGE_URL);
            }

            if (bundle.containsKey(ActionConstants.ORDER_REASON)) {
                reason = bundle.getString(ActionConstants.ORDER_REASON);
            }

            if (bundle.containsKey(ActionConstants.ORDER_QUANTITY)) {
                quantity = bundle.getString(ActionConstants.ORDER_QUANTITY);
            }

            if (bundle.containsKey(ActionConstants.ORDER_SUBTOTAL_ITEM)) {
                totalItems = bundle.getString(ActionConstants.ORDER_SUBTOTAL_ITEM);
            }
        }

        TextView mContactUsText = (TextView) v.findViewById(R.id.bottom_contact_us_text);
        mContactUsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showContactUs(true, Integer.parseInt(invoiceNumber), itemDescription);
                BottomSheetOrderDialog.this.dismiss();
            }
        });

        TextView mOrderText = (TextView) v.findViewById(R.id.bottom_where_is_order_text);
        mOrderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showWhereIsOrder();
                BottomSheetOrderDialog.this.dismiss();
            }
        });

        TextView mChangeAddress = (TextView) v.findViewById(R.id.bottom_change_address_text);
        mChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showChangeAddress(orderID);
                BottomSheetOrderDialog.this.dismiss();
            }
        });

        TextView mReturnItem = (TextView) v.findViewById(R.id.bottom_return_item_text);
        mReturnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showReturnItems(Integer.parseInt(invoiceNumber), true, productID);
                BottomSheetOrderDialog.this.dismiss();
            }
        });

        TextView mCancelOrder = (TextView) v.findViewById(R.id.bottom_view_cancel);
        mCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetOrderDialog.this.dismiss();

                if (!isItemCancel) {
                    MainActivity.showCancelDialog(invoiceNumber, reason);
                } else {
                    MainActivity.showCancelItemDialog(imageUrl, itemDescription, invoiceNumber, reason,
                            Integer.parseInt(quantity), Integer.parseInt(totalItems));
                }
            }
        });

        TextView mViewReturnItem = (TextView) v.findViewById(R.id.bottom_view_return_text);
        mViewReturnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showViewReturnDetails(itemReturnID, itemDescription, true);
                BottomSheetOrderDialog.this.dismiss();
            }
        });

        // Determine which button will be shown
        if (arrayList.size() != 0) {
            if (arrayList.contains(ActionConstants.ORDER_ACTION_CHANGE_ADDRESS)) {
                mChangeAddress.setVisibility(View.VISIBLE);
            }

            if (arrayList.contains(ActionConstants.ORDER_ITEM_RETURN)) {
                mReturnItem.setVisibility(View.VISIBLE);
            }

            if (arrayList.contains(ActionConstants.ORDER_ACTION_CHECK_STATUS)) {
                mContactUsText.setVisibility(View.VISIBLE);
            }

            if (arrayList.contains(ActionConstants.ORDER_ITEM_VIEW_RETURN)) {
                mViewReturnItem.setVisibility(View.VISIBLE);
            }

            isItemCancel = arrayList.contains(ActionConstants.ORDER_ITEM_ACTION_REFUND);

            if (arrayList.contains(ActionConstants.ORDER_ITEM_ACTION_REFUND) ||
                arrayList.contains(ActionConstants.ORDER_ACTION_REFUND)) {

                mCancelOrder.setVisibility(View.VISIBLE);

            }

        }

        return v;
    }

}
