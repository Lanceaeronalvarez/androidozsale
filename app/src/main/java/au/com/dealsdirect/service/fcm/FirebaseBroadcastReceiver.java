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
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.main.MainActivity;

/**
 * Created by MTC on 2019-05-17.
 */
public class FirebaseBroadcastReceiver extends FirebaseMessagingService {

    private NotificationManager notificationManager;
    private String notificationTitle = "";

    @Override
    public void onMessageReceived(RemoteMessage message){

        if (message.getNotification() != null) {
            notificationTitle = message.getNotification().getTitle();
            createNotification(message.getNotification().getBody(), getApplicationContext());
        }

    }

    public void createNotification(String notificationMessage, Context context) {
        final int NOTIFY_ID = 12345;
        String GENERAL_CHANNEL_ID = "GENERAL_CHANNEL_01";
        String appName = getResources().getString(R.string.app_name);
        String title = (notificationTitle == null || notificationTitle.isEmpty()) ? getResources().getString(R.string.app_name) :
                notificationTitle;
        NotificationCompat.Builder builder;
        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(GNotification.FCM_INTENT_LAUNCHED, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        if (notificationManager == null) {
            notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        builder = new NotificationCompat.Builder(context, GENERAL_CHANNEL_ID);
        builder.setContentTitle(title)
                .setSmallIcon(R.drawable.notif_small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentText(notificationMessage)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(notificationMessage)
                .setSound(notificationSoundURI)
                .setPriority(Notification.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(GENERAL_CHANNEL_ID);
            if (mChannel == null) {
                mChannel = new NotificationChannel(GENERAL_CHANNEL_ID, appName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            Notification notification = builder.build();
            startForeground(NOTIFY_ID, notification);
        }

        Notification notification = builder.build();
        notificationManager.notify(NOTIFY_ID, notification);
    }
}
