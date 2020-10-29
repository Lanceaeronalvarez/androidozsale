package au.com.dealsdirect.ui.controller.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import au.com.dealsdirect.R;

public class BottomSheetOrderSatisfactionDialog extends BottomSheetDialogFragment {

    private View mMainLayout = null;

    public final static int POSITIVE_RESPONSE = 1;
    public final static int NEUTRAL_RESPONSE = 0;
    public final static int NEGATIVE_RESPONSE = -1;

    private ImageView positiveResponseImage;
    private ImageView neutralResponseImage;
    private ImageView negativeResponseImage;

    private OnResponseSelectedListener listener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) mMainLayout.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_order_satisfaction, container, false);

        bindViews(v);

        mMainLayout = v;

        setupSatisfactionButtons();

        return v;
    }

    private void bindViews(View v) {
        positiveResponseImage = v.findViewById(R.id.order_satisfaction_positive);
        neutralResponseImage = v.findViewById(R.id.order_satisfaction_neutral);
        negativeResponseImage = v.findViewById(R.id.order_satisfaction_negative);
    }

    private void setupSatisfactionButtons() {
        positiveResponseImage.setOnClickListener(v -> {
            if (listener != null) {
                listener.onResponseSelected(POSITIVE_RESPONSE);
            }
            dismiss();
        });

        neutralResponseImage.setOnClickListener(v -> {
            if (listener != null) {
                listener.onResponseSelected(NEUTRAL_RESPONSE);
            }
            dismiss();
        });

        negativeResponseImage.setOnClickListener(v -> {
            if (listener != null) {
                listener.onResponseSelected(NEGATIVE_RESPONSE);
            }
            dismiss();
        });
    }

    public OnResponseSelectedListener getListener() {
        return listener;
    }

    public void setListener(OnResponseSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnResponseSelectedListener {
        void onResponseSelected(int response);
    }
}
