package au.com.dealsdirect.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/*
 * Created by Ayi on 29/05/2017.
 */

public class DialogUtils {

    public static void showYesNoDialog(Context context, String title, DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(title)
                .setPositiveButton("Yes", onClickListener)
                .setNegativeButton("No", onClickListener)
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
