package au.com.dealsdirect.ui.controller.orders.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.utils.ActionConstants;

/**
 * Created by MTC on 2019-06-21.
 */
public class BottomSheetOrderDialog extends BottomSheetDialogFragment {

    private ArrayList<String> arrayList = new ArrayList<>();
    private BottomSheetButtonListener listener;

    public BottomSheetOrderDialog(@NonNull BottomSheetButtonListener listener) {
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

            if (bundle.containsKey(ActionConstants.ORDER_ARRAYS)) {
                arrayList = bundle.getStringArrayList(ActionConstants.ORDER_ARRAYS);
            }
        }

        TextView mContactUsText = v.findViewById(R.id.bottom_contact_us_text);
        mContactUsText.setOnClickListener(v6 -> {
            BottomSheetOrderDialog.this.dismiss();
            listener.onContactUsPressed();
        });

        TextView mOrderText = v.findViewById(R.id.bottom_where_is_order_text);
        mOrderText.setOnClickListener(v5 -> {
            BottomSheetOrderDialog.this.dismiss();
            listener.onOrderPressed();
        });

        TextView mChangeAddress = v.findViewById(R.id.bottom_change_address_text);
        mChangeAddress.setOnClickListener(v4 -> {
            BottomSheetOrderDialog.this.dismiss();
            listener.onChangeAddressPressed();
        });

        TextView mReturnItem = v.findViewById(R.id.bottom_return_item_text);
        mReturnItem.setOnClickListener(v3 -> {
            BottomSheetOrderDialog.this.dismiss();
            listener.onReturnItemPressed();
        });

        TextView mCancelOrder = v.findViewById(R.id.bottom_view_cancel);
        mCancelOrder.setOnClickListener(v2 -> {
            BottomSheetOrderDialog.this.dismiss();
            listener.onCancelOrderPressed();
        });

        TextView mViewReturnItem = v.findViewById(R.id.bottom_view_return_text);
        mViewReturnItem.setOnClickListener(v1 -> {
            BottomSheetOrderDialog.this.dismiss();
            listener.onViewReturnItemPressed();
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

            if (arrayList.contains(ActionConstants.ORDER_ITEM_ACTION_REFUND) ||
                    arrayList.contains(ActionConstants.ORDER_ACTION_REFUND)) {

                mCancelOrder.setVisibility(View.VISIBLE);

            }

            if (mChangeAddress.getVisibility() != View.VISIBLE &&
                    mReturnItem.getVisibility() != View.VISIBLE &&
                    mContactUsText.getVisibility() != View.VISIBLE &&
                    mViewReturnItem.getVisibility() != View.VISIBLE &&
                    mCancelOrder.getVisibility() != View.VISIBLE) {
                mContactUsText.setVisibility(View.VISIBLE);
            }

        }

        return v;
    }

    public interface BottomSheetButtonListener {
        void onContactUsPressed();

        void onOrderPressed();

        void onChangeAddressPressed();

        void onReturnItemPressed();

        void onCancelOrderPressed();

        void onViewReturnItemPressed();
    }
}
