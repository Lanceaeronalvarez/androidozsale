package au.com.dealsdirect.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;

public class ActivityLaunchUtil {

    public static void launchActivity(@NonNull Activity activity, String url) {
        launchActivity(activity, url, null);
    }

    public static void launchActivity(@NonNull Activity activity, String url, String errorMessage) {
        try {
            Uri uri = getUri(url);
            if (uri == null) throw new ActivityNotFoundException();

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(activity.getPackageManager()) == null) {
                throw new ActivityNotFoundException();
            }

            activity.startActivity(intent);

        } catch (ActivityNotFoundException e) {
            AppLogger.d("ActivityNotFoundException: " + e.getMessage());

            String customAlertDialogMessage = errorMessage == null || errorMessage.isEmpty() ?
                    activity.getString(R.string.an_error_has_occurred) : errorMessage;

            CustomAlertDialog.showCustomAlertDialog(activity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    customAlertDialogMessage);
        }
    }

    private static Uri getUri(String link) {
        if (link == null || link.isEmpty()) return null;

        try {
            return Uri.parse(link);
        } catch (RuntimeException exception) {
            AppLogger.d("RuntimeException: " + exception.getMessage());
            return null;
        }
    }
}
