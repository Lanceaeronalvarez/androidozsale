package au.com.dealsdirect.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Admin on 6/8/18.
 */

public class ProgressUtil {

    public ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog dialog = ProgressDialog.show(context, "",
                "Loading. Please wait...", true);

        return dialog;
    }
}
