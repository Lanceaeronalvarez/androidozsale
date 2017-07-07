package au.com.dealsdirect.ui.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.dealsdirect.R;

/**
 * Created by Paul on 7/5/17.
 */

public class CustomAlertDialog {

    private static final int WINDOW_DIM_AMOUNT = 0;
    private static final int DISMISS_DELAY = 2000;

    private static android.support.v7.app.AlertDialog alertDialog;

    private static int showCount = 0;

    public enum CustomDialogIconState {
        POSITIVE,
        NEGATIVE
    }

    public enum CustomDialogGravity {
        TOP,
        BOTTOM
    }

    public static android.support.v7.app.AlertDialog showCustomAlertDialogWithTextLink(
            Activity activity,
            CustomDialogIconState customDialogIconState,
            String description,
            String clickableText,
            View.OnClickListener clickListener) {

        alertDialog = null;

        LayoutInflater inflater = activity.getLayoutInflater();

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);

        ImageView mDialogIcon =
                (ImageView) dialogView.findViewById(R.id.dialog_alert_icon);
        TextView mDialogDescription =
                (TextView) dialogView.findViewById(R.id.dialog_alert_description);
        TextView mDialogTextLink =
                (TextView) dialogView.findViewById(R.id.dialog_alert_clickable_text);


        setAlertDialogDrawable(mDialogIcon, customDialogIconState);
        mDialogDescription.setText(description);

        SpannableString content = new SpannableString(clickableText);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mDialogTextLink.setText(content);
        mDialogTextLink.setPaintFlags(mDialogTextLink.getPaintFlags() |
                Paint.UNDERLINE_TEXT_FLAG);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        builder.setView(dialogView);
        android.support.v7.app.AlertDialog newAlertDialog = builder.create();
        newAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setAlertDialogGravity(newAlertDialog, CustomDialogGravity.TOP);
        newAlertDialog.getWindow()
                .getAttributes().windowAnimations = R.style.AppearDialog;

        newAlertDialog.getWindow().setDimAmount(WINDOW_DIM_AMOUNT);
        mDialogTextLink.setOnClickListener(clickListener);

        if (showCount != 1) {
            newAlertDialog.show();

        }
        showCount = 1;
        dismissOnDelay();

        newAlertDialog.setOnDismissListener(dialogInterface -> showCount = 0);

        alertDialog = newAlertDialog;

        return newAlertDialog;

    }


    public static android.support.v7.app.AlertDialog showCustomAlertDialog(
            Activity activity,
            CustomDialogIconState customDialogIconState,
            String description) {

        alertDialog = null;

        LayoutInflater inflater = activity.getLayoutInflater();

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);

        ImageView mDialogIcon =
                (ImageView) dialogView.findViewById(R.id.dialog_alert_icon);
        TextView mDialogDescription =
                (TextView) dialogView.findViewById(R.id.dialog_alert_description);
        TextView mDialogTextLink =
                (TextView) dialogView.findViewById(R.id.dialog_alert_clickable_text);


        mDialogTextLink.setVisibility(View.GONE);
        setAlertDialogDrawable(mDialogIcon, customDialogIconState);
        mDialogDescription.setText(description);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        builder.setView(dialogView);

        android.support.v7.app.AlertDialog newAlertDialog = builder.create();
        newAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setAlertDialogGravity(newAlertDialog, CustomDialogGravity.TOP);
        newAlertDialog.getWindow()
                .getAttributes().windowAnimations = R.style.AppearDialog;

        newAlertDialog.getWindow().setDimAmount(WINDOW_DIM_AMOUNT);
        newAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);


        if (showCount != 1) {
            newAlertDialog.show();
        }
        showCount = 1;
        dismissOnDelay();

        newAlertDialog.setOnDismissListener(dialogInterface -> showCount = 0);

        alertDialog = newAlertDialog;
        return newAlertDialog;
    }

    private static void setAlertDialogGravity(
            android.support.v7.app.AlertDialog alertDialog,
            CustomDialogGravity customDialogGravity) {

        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        switch (customDialogGravity) {

            case TOP:
                wlp.gravity = Gravity.TOP;
                break;

            case BOTTOM:
                wlp.gravity = Gravity.BOTTOM;
                break;

            default:
                wlp.gravity = Gravity.CENTER;
                break;
        }

        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

    }


    private static void setAlertDialogDrawable(
            ImageView customDialogIcon,
            CustomDialogIconState customDialogIconState) {

        switch (customDialogIconState) {
            case POSITIVE:
                customDialogIcon.setImageResource(R.drawable.ic_alert_dialog_check);
                break;
            case NEGATIVE:
                customDialogIcon.setImageResource(R.drawable.ic_alert_dialog_cross);
                break;
            default:
                customDialogIcon.setImageResource(R.drawable.ic_alert_dialog_check);
                break;
        }
    }


    private static void dismissOnDelay() {

        Handler handler = new Handler();
        Runnable runnable = CustomAlertDialog::dismissCustomDialog;

        handler.postDelayed(runnable, DISMISS_DELAY);
    }

    public static void dismissCustomDialog() {

        try {
            if ((alertDialog != null) && alertDialog.isShowing()) {
                alertDialog.dismiss();
                showCount = 0;
            }
            alertDialog = null;

        } catch (IllegalArgumentException e) {
            //Window is leaked cause of activity change
            alertDialog = null;
        }
    }
}
