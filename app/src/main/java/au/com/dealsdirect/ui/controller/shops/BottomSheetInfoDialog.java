package au.com.dealsdirect.ui.controller.shops;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import au.com.dealsdirect.R;

/**
 * Created by MTC on 2020-03-31.
 */
public class BottomSheetInfoDialog extends BottomSheetDialogFragment {

    private String title = "";
    private String description = "";
    private String buttonTitle = "";
    private OnButtonClickListener onButtonClickListener = null;
    private boolean dismissOnButtonClick = true;

    private int layoutId = R.layout.bottom_sheet_info;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(layoutId, container, false);

        final TextView textViewDescription = v.findViewById(R.id.bottom_sheet_info_description);
        final TextView textViewTitle = v.findViewById(R.id.bottom_sheet_info_title);
        final Button button = v.findViewById(R.id.bottom_sheet_info_button);

        textViewDescription.setText(getDescription());
        textViewTitle.setText(getTitle());

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
}
