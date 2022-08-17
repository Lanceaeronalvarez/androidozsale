package au.com.dealsdirect.ui.controller.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import au.com.dealsdirect.R;

import static android.view.Gravity.CENTER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by MTC on 2020-03-31.
 */
public class AccountDeletionConfirmationDialog extends DialogFragment {

    public enum Result {
        CONFIRMED,
        CANCELLED
    }

    private DismissListener dismissListener = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_account_deletion_confirmation, container, false);

        final Button confirmButton = v.findViewById(R.id.account_deletion_confirmation_confirm_button);
        final Button cancelButton = v.findViewById(R.id.account_deletion_confirmation_cancel_button);
        final ImageButton closeButton = v.findViewById(R.id.account_deletion_confirmation_close_button);

        confirmButton.setOnClickListener(v1 -> onButtonClick(Result.CONFIRMED));

        cancelButton.setOnClickListener(v1 -> onButtonClick(Result.CANCELLED));

        closeButton.setOnClickListener(v1 -> onButtonClick(Result.CANCELLED));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                window.setLayout(MATCH_PARENT, WRAP_CONTENT);
                window.setGravity(CENTER);
            }
        }
    }

    private void onButtonClick(Result result) {
        dismiss();
        if (dismissListener != null) {
            dismissListener.onDismiss(result);
        }
    }

    public void setDismissListener(DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public interface DismissListener {
        void onDismiss(Result result);
    }
}
