/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.dealsdirect.service.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import au.com.dealsdirect.DDApplication;
import au.com.dealsdirect.R;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.AppLogger;


/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends JobIntentService {

    DataManager mDataManager;

    public GcmIntentService() {
        super();
    }

    public static final String TAG = "GCM";
    private final String GENERAL_CHANNEL_ID = "GENERAL_CHANNEL_01";
    private static final int JOB_ID = 1000;

    @Override
    protected void onHandleWork(Intent intent) {

        mDataManager = ((DDApplication) getApplication()).getComponent().getDataManager();

        if (!mDataManager.getIsNotificationsEnabled()) {
            return;
        }

        Bundle extras = intent.getExtras();
        String message = "";
        if (extras != null && extras.get("alert") != null) {
            message = (String) extras.get("alert");
        }
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + message);

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + message);

                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
                sendNotification(extras.getString("alert"));
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    public void sendNotification(String msg) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(GNotification.FCM_INTENT_LAUNCHED, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String appName = getResources().getString(R.string.app_name);

        Uri uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, GENERAL_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_loader_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(appName)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(uriSound)
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

        NotificationChannel generalChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            generalChannel = new NotificationChannel(GENERAL_CHANNEL_ID, "General", NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(generalChannel);

            Notification notification = new Notification.Builder(getApplicationContext(),GENERAL_CHANNEL_ID).build();
            startForeground(1, notification);
        }
    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GcmIntentService.class, JOB_ID, intent);
    }

}
