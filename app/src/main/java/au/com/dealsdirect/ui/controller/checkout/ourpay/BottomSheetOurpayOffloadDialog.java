package au.com.dealsdirect.ui.controller.checkout.ourpay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Map;

import au.com.dealsdirect.R;

/**
 * Created by MTC on 2020-03-31.
 */
public class BottomSheetOurpayOffloadDialog extends BottomSheetDialogFragment {

    private static final int BUTTON_CLOSE = 0;
    private static final int BUTTON_KLARNA = 1;
    private static final int BUTTON_OTHER = 2;

    private final Map<Integer, View.OnClickListener> onClickListeners = new HashMap<>(3);
    private final Map<Integer, View> clickableViews = new HashMap<>(3);

    private View klarnaSection = null;
    private boolean isKlarnaSectionVisible = true;

    private int layoutId = R.layout.bottom_sheet_ourpay_offload;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(layoutId, container, false);

        clickableViews.put(BUTTON_CLOSE, v.findViewById(R.id.offload_ourpay_close_button));
        clickableViews.put(BUTTON_KLARNA, v.findViewById(R.id.offload_ourpay_klarna_button));
        clickableViews.put(BUTTON_OTHER, v.findViewById(R.id.offload_ourpay_other_button));

        klarnaSection = v.findViewById(R.id.offload_ourpay_klarna_section);

        setupButtons();
        setupKlarnaSection();

        return v;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    private void setupButtons() {
        for (int key : clickableViews.keySet()) {
            setupButton(key);
        }
    }

    private void setupButton(int id) {
        View view = clickableViews.get(id);
        View.OnClickListener onClickListener = onClickListeners.get(id);
        if (view != null) {
            view.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onClick(view);
                }
                dismiss();
            });
        }
    }

    public void setOnCloseButtonClickListener(View.OnClickListener onCloseButtonClickListener) {
        onClickListeners.put(BUTTON_CLOSE, onCloseButtonClickListener);
        setupButton(BUTTON_CLOSE);
    }

    public void setOnKlarnaButtonClickListener(View.OnClickListener onKlarnaButtonClickListener) {
        onClickListeners.put(BUTTON_KLARNA, onKlarnaButtonClickListener);
        setupButton(BUTTON_KLARNA);
    }

    public void setOnOtherPaymentsButtonClickListener(View.OnClickListener onOtherPaymentsButtonClickListener) {
        onClickListeners.put(BUTTON_OTHER, onOtherPaymentsButtonClickListener);
        setupButton(BUTTON_OTHER);
    }

    public boolean isKlarnaSectionVisible() {
        return isKlarnaSectionVisible;
    }

    public void setIsKlarnaVisible(boolean isVisible) {
        isKlarnaSectionVisible = isVisible;
        setupKlarnaSection();
    }

    private void setupKlarnaSection() {
        if (klarnaSection == null) {
            return;
        }
        klarnaSection.setVisibility(isKlarnaSectionVisible ? View.VISIBLE : View.GONE);
    }
}
