package au.com.dealsdirect.ui.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import au.com.dealsdirect.R;
import au.com.dealsdirect.utils.ImageUtils;

/*
 * Created by Paul on 7/5/17.
 */

public class CustomAlertDialog {

    private static final int WINDOW_DIM_AMOUNT = 0;
    private static final int DISMISS_DELAY = 2000;

    private static AlertDialog alertDialog;

    private static int showCount = 0;

    public enum CustomDialogIconState {
        POSITIVE,
        NEGATIVE
    }

    public enum CustomDialogGravity {
        TOP,
        BOTTOM,
        CENTER
    }

    public static AlertDialog showCustomAlertDialogWithTextLink(
            Activity activity,
            CustomDialogIconState customDialogIconState,
            String description,
            String clickableText,
            View.OnClickListener clickListener) {

        if (alertDialog != null)
            alertDialog.dismiss();

        LayoutInflater inflater = activity.getLayoutInflater();

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);

        ImageView mDialogIcon = dialogView.findViewById(R.id.dialog_alert_icon);
        TextView mDialogDescription = dialogView.findViewById(R.id.dialog_alert_description);
        TextView mDialogTextLink = dialogView.findViewById(R.id.dialog_alert_clickable_text);


        setAlertDialogDrawable(mDialogIcon, customDialogIconState);
        mDialogDescription.setText(description);

        SpannableString content = new SpannableString(clickableText);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mDialogTextLink.setText(content);
        mDialogTextLink.setPaintFlags(mDialogTextLink.getPaintFlags() |
                Paint.UNDERLINE_TEXT_FLAG);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity.getApplicationContext());
        builder.setView(dialogView);
        AlertDialog newAlertDialog = builder.create();

        if (newAlertDialog.getWindow() != null) {
            newAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        setAlertDialogGravity(newAlertDialog, CustomDialogGravity.TOP);
        newAlertDialog.getWindow()
                .getAttributes().windowAnimations = R.style.AppearDialog;

        newAlertDialog.getWindow().setDimAmount(WINDOW_DIM_AMOUNT);
        mDialogTextLink.setOnClickListener(clickListener);

        if (showCount != 1) {
            try {
                newAlertDialog.show();
            } catch (Exception e) {
                Log.d(CustomAlertDialog.class.getName(), e.getMessage());
            }
        }
        showCount = 1;
        dismissOnDelay();

        newAlertDialog.setOnDismissListener(dialogInterface -> showCount = 0);

        alertDialog = newAlertDialog;

        return newAlertDialog;

    }


    public static AlertDialog showCustomAlertDialog(
            Activity activity,
            CustomDialogIconState customDialogIconState,
            String description) {

        if (alertDialog != null)
            alertDialog.dismiss();

        LayoutInflater inflater = activity.getLayoutInflater();

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);

        ImageView mDialogIcon = dialogView.findViewById(R.id.dialog_alert_icon);
        TextView mDialogDescription = dialogView.findViewById(R.id.dialog_alert_description);
        TextView mDialogTextLink = dialogView.findViewById(R.id.dialog_alert_clickable_text);


        mDialogTextLink.setVisibility(View.GONE);
        setAlertDialogDrawable(mDialogIcon, customDialogIconState);
        mDialogDescription.setText(description);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);

        AlertDialog newAlertDialog = builder.create();
        if (newAlertDialog.getWindow() != null) {
            newAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        setAlertDialogGravity(newAlertDialog, CustomDialogGravity.TOP);
        newAlertDialog.getWindow().getAttributes().windowAnimations = R.style.AppearDialog;

        newAlertDialog.getWindow().setDimAmount(WINDOW_DIM_AMOUNT);
        // Elv - 11/23/17 - this line of code causes dialog not to show for Oreo
        //newAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);


        if (showCount != 1) {
            if (activity != null) {
                try {
                    newAlertDialog.show();
                    showCount = 1;
                } catch (Exception e) {
                    Log.d(CustomAlertDialog.class.getName(), e.getMessage());
                }
            }
        }

        newAlertDialog.setOnDismissListener(dialogInterface -> showCount = 0);
        alertDialog = newAlertDialog;
        dismissOnDelay();

        return newAlertDialog;

    }

    public static AlertDialog showCustomCancelOrderDialog(
            Activity activity,
            String orderNumber,
            CustomDialogButtonListener listener) {


        LayoutInflater inflater = activity.getLayoutInflater();

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_cancel_order, null);

        Button mYesButton = dialogView.findViewById(R.id.button_yes_cancel_order);
        Button mNoButton = dialogView.findViewById(R.id.button_no_cancel_order);
        ImageButton mCloseButton = dialogView.findViewById(R.id.img_order_button_close);
        TextView mTextOrderNumber = dialogView.findViewById(R.id.cancel_order_number_text);


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);

        AlertDialog newAlertDialog = builder.create();

        setAlertDialogGravity(newAlertDialog, CustomDialogGravity.CENTER);
        if (newAlertDialog.getWindow() != null) {
            newAlertDialog.getWindow().getAttributes().windowAnimations = R.style.CancelOrderDialog;
            newAlertDialog.getWindow().setDimAmount(WINDOW_DIM_AMOUNT);
        }

        mTextOrderNumber.setText(orderNumber);

        newAlertDialog.show();

        mYesButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onYes(null);
            }
            dismissCustomDialog();
        });

        mNoButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNo(null);
            }
            dismissCustomDialog();
        });

        mCloseButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClose(null);
            }
            dismissCustomDialog();
        });

        alertDialog = newAlertDialog;

        return newAlertDialog;
    }

    public static AlertDialog showCancelItemDialog(
            Activity activity,
            String imageUrl,
            String itemName,
            int quantity,
            int totalItems,
            CustomDialogButtonListener listener) {


        LayoutInflater inflater = activity.getLayoutInflater();

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_cancel_item_order, null);

        Button mYesButton = dialogView.findViewById(R.id.button_yes_cancel_item);
        Button mNoButton = dialogView.findViewById(R.id.button_no_cancel_item);
        ImageButton mCloseButton = dialogView.findViewById(R.id.img_button_close);
        ImageView mItemImage = dialogView.findViewById(R.id.item_cancel_image);
        TextView mItemName = dialogView.findViewById(R.id.item_name);
        ProductQuantityLayout mQuantity = dialogView.findViewById(R.id.item_quantity);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);

        AlertDialog newAlertDialog = builder.create();

        setAlertDialogGravity(newAlertDialog, CustomDialogGravity.CENTER);
        if (newAlertDialog.getWindow() != null) {
            newAlertDialog.getWindow().getAttributes().windowAnimations = R.style.CancelOrderDialog;
            newAlertDialog.getWindow().setDimAmount(WINDOW_DIM_AMOUNT);
        }

        mItemName.setText(itemName);

        ImageUtils.loadImage(imageUrl, mItemImage);

        mQuantity.setQuantity(quantity);
        mQuantity.setAutoUpdateQuantity(false);
        mQuantity.setMax(totalItems);
        mQuantity.setEditTextToNonEditable();

        mQuantity.setOnQuantityChangeListener(new ProductQuantityLayout.onQuantityChangeListener() {
            @Override
            public void onQuantityIncrease(ProductQuantityLayout view, int value) {
                if (value < totalItems) {
                    value++;
                }
                mQuantity.setQuantity(value);
            }

            @Override
            public void onQuantityDecrease(ProductQuantityLayout view, int value) {
                if (value != 1) {
                    value--;
                }
                mQuantity.setQuantity(value);
            }
        });

        newAlertDialog.show();

        mYesButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onYes(mQuantity.getQuantity());
            }
            dismissCustomDialog();
        });

        mNoButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNo(null);
            }
            dismissCustomDialog();
        });

        mCloseButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClose(null);
            }
            dismissCustomDialog();
        });

        alertDialog = newAlertDialog;

        return newAlertDialog;
    }

    private static void setAlertDialogGravity(
            AlertDialog alertDialog,
            CustomDialogGravity customDialogGravity) {

        Window window = alertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();

            switch (customDialogGravity) {

                case TOP:
                    wlp.gravity = Gravity.TOP;
                    break;

                case BOTTOM:
                    wlp.gravity = Gravity.BOTTOM;
                    break;

                case CENTER:
                    wlp.gravity = Gravity.CENTER;
                    break;

                default:
                    wlp.gravity = Gravity.CENTER;
                    break;
            }

            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
        }
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

    private static void dismissCustomDialog() {
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

    public interface CustomDialogButtonListener {
        void onYes(Object object);

        void onNo(Object object);

        void onClose(Object object);
    }
}
