package au.com.dealsdirect.ui.controller.shops;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import au.com.dealsdirect.R;

public class BottomSheetInfoDialog extends BottomSheetDialogFragment {

    private String title = "";
    private String description = "";
    private String buttonTitle = "";
    private OnButtonClickListener onButtonClickListener = null;
    private boolean dismissOnButtonClick = true;

    private int layoutId = R.layout.bottom_sheet_info;

    private ScrollView contentScrollview = null;
    private BottomSheetBehavior<View> behavior = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            behavior = BottomSheetBehavior.from((View) getView().getParent());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(layoutId, null);

        final TextView textViewDescription = v.findViewById(R.id.bottom_sheet_info_description);
        final TextView textViewTitle = v.findViewById(R.id.bottom_sheet_info_title);
        final Button button = v.findViewById(R.id.bottom_sheet_info_button);
        contentScrollview = v.findViewById(R.id.bottom_sheet_info_content_scrollview);

        textViewDescription.setText(getDescription());
        textViewTitle.setText(getTitle());

        textViewDescription.setVisibility(getDescription() == null ? View.GONE : View.VISIBLE);
        textViewTitle.setVisibility(getTitle() == null ? View.GONE : View.VISIBLE);

        if (button != null) {
            if (shouldHideButton()) {
                button.setVisibility(View.GONE);
            } else {
                button.setVisibility(View.VISIBLE);
                button.setText(buttonTitle);
                button.setOnClickListener(v1 -> {
                    if (dismissOnButtonClick) {
                        dismiss();
                    }
                    if (onButtonClickListener != null) {
                        onButtonClickListener.onClick();
                    }
                });
            }
        }

        if (contentScrollview != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                contentScrollview.setOnScrollChangeListener((v12, scrollX, scrollY, oldScrollX, oldScrollY) -> onScrollChanged(scrollY));
            } else {
                contentScrollview.getViewTreeObserver().addOnScrollChangedListener(() -> BottomSheetInfoDialog.this.onScrollChanged(contentScrollview.getScrollY()));
            }
        }


        return v;
    }

    private boolean shouldHideButton() {
        return buttonTitle == null || onButtonClickListener == null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public void setupButton(String buttonTitle, OnButtonClickListener onButtonClickListener) {
        this.buttonTitle = buttonTitle;
        this.onButtonClickListener = onButtonClickListener;
    }

    public Boolean isDismissOnButtonClick() {
        if (shouldHideButton()) {
            return null;
        } else {
            return dismissOnButtonClick;
        }
    }

    public void setDismissOnButtonClick(boolean dismissOnButtonClick) {
        this.dismissOnButtonClick = dismissOnButtonClick;
    }

    public interface OnButtonClickListener {
        void onClick();
    }

    private void onScrollChanged(int scrollY) {
        if (behavior == null) {
            return;
        }
        behavior.setDraggable(scrollY <= 0);
    }
}
