package au.com.dealsdirect.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/*
 * Created by Ayi on 29/05/2017.
 */

public class DialogUtils {

    public static void showYesNoDialog(Context context, String title, String messageYes, String messageNo
            ,DialogInterface.OnClickListener positiveOnClickListener
            ,DialogInterface.OnClickListener negativeOnClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(title)
                .setCancelable(false)
                .setPositiveButton("Yes", positiveOnClickListener)
                .setNegativeButton("No", negativeOnClickListener)
                .show();
    }

    public static void showYesDialog(Context context, String title, String message, String option1, DialogInterface.OnClickListener positiveOnClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(message, positiveOnClickListener)
                .show();
    }

    public static void showYesNoDialogWithDismissListener(Context context, String title,
                                                         DialogInterface.OnClickListener onClickListener,
                                                         DialogInterface.OnDismissListener onDismissListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(title)
                .setPositiveButton("Yes", onClickListener)
                .setNegativeButton("No", onClickListener)
                .setOnDismissListener(onDismissListener)
                .show();
    }

}
