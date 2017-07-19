package au.com.dealsdirect.service.fcm;
/*
 * Created by CodeineBot on 1/26/17.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mysale.genie.utility.Prefs;

import java.io.IOException;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.fcm.NotificationEvent;
import au.com.dealsdirect.data.network.model.fcm.RegisterDevice;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class GNotification {

    private static final String TAG = "GCM";
    public static final String PROPERTY_REG_ID = "gcm_reg_id";
    public static final String PROPERTY_APP_VERSION = "gcm_app_version";
    private static final String SENDER_ID = "177836257791"; //Api project ID - Google console
    public static final String PROPERTY_REGISTERED = "gcm_is_registered";

    public static final String FCM_INTENT_LAUNCHED = "fcm_intent_launched";

    @Inject
    DataManager mDataManager;
    @Inject
    SchedulerProvider mSchedulerProvider;
    @Inject
    CompositeDisposable mCompositeDisposable;

    public GNotification(){

    }

    public void callRegisterDevice(Context context, String token) {

        RegisterDevice.RequestValue requestValue = new RegisterDevice.RequestValue();
        requestValue.app = getNotificationServerName(context);
        requestValue.deviceID = getDeviceID(context);
        requestValue.platform = "android";
        requestValue.token = token;
        requestValue.manufacture = Build.MODEL;
        requestValue.model = mDataManager.getLoginTicket();
        requestValue.autologinTicket = mDataManager.getLoginTicket();

        mCompositeDisposable.add(mDataManager.callRegisterDevice(requestValue)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Consumer<RegisterDevice.ResponseValue>() {
                    @Override
                    public void accept(@NonNull RegisterDevice.ResponseValue responseValue) throws Exception {

                    mDataManager.setIsGCMRegistered(responseValue.getD().getValue().getRegistered());
                    AppLogger.d(TAG, "Device Registered to FCM and APAC Server: " + responseValue.getD().getValue().getRegistered());

                    }
                })
        );
    }

    public void callNotificationEvent(Context context) {

        NotificationEvent.RequestValue requestValue = new NotificationEvent.RequestValue();
        requestValue.eventName = "";
        requestValue.messageID = "";
        requestValue.autologinTicket = mDataManager.getLoginTicket();

        mCompositeDisposable.add(mDataManager.callNotificationEvent(requestValue)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Consumer<NotificationEvent.ResponseValue>() {
                    @Override
                    public void accept(@NonNull NotificationEvent.ResponseValue responseValue) throws Exception {
                        AppLogger.d(TAG, "Notification Event Called");
                    }
                })
        );
    }

    private boolean checkPlayServices(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status) && context instanceof Activity) {
                googleApiAvailability.getErrorDialog((Activity) context, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        String registrationId = Prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            AppLogger.d(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = mDataManager.getGCMAppVersion();
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            AppLogger.d(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        AppLogger.d(TAG, "Saving regId on app version " + appVersion);
        mDataManager.setGCMRegistrationId(regId);
        mDataManager.setGCMAppVersion(appVersion);
    }

    public void registerDeviceForNotification(Context context) {
        if (checkPlayServices(context)) {
            AppLogger.d(TAG, "checkPlayServices true");
            String regId = getRegistrationId(context.getApplicationContext());
            AppLogger.d(TAG, "regId " + regId);
            int registeredToServer = Prefs.getInt(PROPERTY_REGISTERED, -1);
            if (regId.isEmpty()) {
                new RegisterInBackground().execute(context);
            } else {
                if (registeredToServer != 0) {
                    callRegisterDevice(context, regId);
                }
            }
        } else {
            AppLogger.d(TAG, "No valid Google Play Services APK found.");
        }
    }

    private String getNotificationServerName(Context context) {
        String appName = context.getResources().getString(R.string.app_name);
        appName = appName.replace(" ", "-").toLowerCase();
        AppLogger.d(TAG, "ServerName: " + appName);
        return appName;
    }

    private String getDeviceID(Context context) {
        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String serial = android.os.Build.SERIAL;
        AppLogger.d(TAG, "Settings.Secure.ANDROID_ID " + android_id);
        AppLogger.d(TAG, "serial " + serial);
        if (!android_id.isEmpty()) return android_id;
        else if (!serial.isEmpty()) return serial;
        else return "NoDeviceIDRetrieved";
    }

    private class RegisterInBackground extends AsyncTask<Context, Void, String> {

        @Override
        protected String doInBackground(Context... params) {

            String msg;
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(params[0]);

            String regId = "";
            try {
                regId = gcm.register(SENDER_ID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            msg = "Device registered, registration ID=" + regId;
            AppLogger.d(TAG, "RegisterInBackground " + msg);

            storeRegistrationId(params[0], regId);

            callRegisterDevice(params[0], regId);

            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            AppLogger.d(TAG, msg);
        }

    }

    public void detach(){
        mCompositeDisposable.dispose();
    }
}
