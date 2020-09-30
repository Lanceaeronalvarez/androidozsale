package au.com.dealsdirect.ui.controller.shops;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.utils.ActionConstants;
import au.com.dealsdirect.utils.AppConstants;

/**
 * Created by MTC on 2020-03-31.
 */
public class BottomSheetFreeShippingDialog extends BottomSheetDialogFragment {

    private String deliveryThreshold = "";
    private String deliveryType = "";
    private String title = "";
    public static final String KEY_SHIPPING_AMOUNT = "[[Amount]]";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_free_shipping_content, container, false);

        TextView mTextViewShippingDescription = v.findViewById(R.id.bottom_sheet_free_shipping_description);
        TextView mShippingTitle = v.findViewById(R.id.bottom_sheet_free_shipping_title);

        String shippingAmount = Settings.getSelectedCountry().currencySign + getDeliveryThreshold();
        String textToDisplay = getDeliveryType().replace(KEY_SHIPPING_AMOUNT, shippingAmount);
        mTextViewShippingDescription.setText(textToDisplay);
        mShippingTitle.setText(getTitle());

        return v;
    }

    public String getDeliveryThreshold() {
        return deliveryThreshold;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryThreshold(String deliveryThreshold) {
        this.deliveryThreshold = deliveryThreshold;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
