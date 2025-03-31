package au.com.dealsdirect.ui.controller.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.custom.BottomPopupView;
import au.com.dealsdirect.ui.custom.DimmedBottomPopupView;

public class SignUpModalBottomPopupAdapter implements BottomPopupView.BottomPopupViewAdapter, DimmedBottomPopupView.DimmedBottomPopupViewAdapter {

    private ViewGroup layout;
    private ViewGroup container;
    private View dimmingView;
    private View subscribeButton;
    private View closeButton;
    private View tnc;

    private OnCloseClickListener onCloseClickListener;

    private OnSubscribeClickListener onSubscribeClickListener;

    private OnTNCClickListener onTNCClickListener;

    @Override
    public View onCreate(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottom_sheet_signup_modal, parent, false);

        container = v.findViewById(R.id.bottom_sheet_signup_modal_container);
        dimmingView = v.findViewById(R.id.bottom_sheet_dimming_view);
        subscribeButton = v.findViewById(R.id.bottom_sheet_signup_modal_button);
        closeButton = v.findViewById(R.id.bottom_sheet_signup_modal_close_button);
        tnc = v.findViewById(R.id.bottom_sheet_signup_modal_tnc_text);
        return v;
    }

    public void setOnCloseClickListener(OnCloseClickListener onCloseClickListener) {
        this.onCloseClickListener = onCloseClickListener;

        if (closeButton != null) {
            if (onCloseClickListener != null) {
                closeButton.setOnClickListener(v -> this.onCloseClickListener.onClick());
            } else {
                closeButton.setOnClickListener(null);
            }
        }
    }

    public void setOnSubscribeClickListener(OnSubscribeClickListener onSubscribeClickListener) {
        this.onSubscribeClickListener = onSubscribeClickListener;

        if (subscribeButton != null) {
            if (onSubscribeClickListener != null) {
                subscribeButton.setOnClickListener(v -> this.onSubscribeClickListener.onClick());
            } else {
                subscribeButton.setOnClickListener(null);
            }
        }
    }

    public void setOnTNCClickListener(OnTNCClickListener onTNCClickListener) {
        this.onTNCClickListener = onTNCClickListener;

        if (tnc != null) {
            if (onTNCClickListener != null) {
                tnc.setOnClickListener(v -> this.onTNCClickListener.onClick());
            } else {
                tnc.setOnClickListener(null);
            }
        }
    }

    @Override
    public View getBottomView() {
        return container;
    }

    @Override
    public View getDimmingView() {
        return dimmingView;
    }

    public interface OnSubscribeClickListener{
        void onClick();
    }

    public interface OnCloseClickListener {
        void onClick();
    }

    public interface OnTNCClickListener {
        void onClick();
    }
}
