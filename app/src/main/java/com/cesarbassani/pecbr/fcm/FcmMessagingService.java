package com.cesarbassani.pecbr.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.model.Notification;
import com.cesarbassani.pecbr.utils.PermissionUtil;
import com.cesarbassani.pecbr.views.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (PermissionUtil.isStorageGranted(this)) {
            Notification notification = new Notification();
            notification.title = getString(R.string.app_name);
            notification.content = remoteMessage.getNotification().getBody();

            // display notification
            displayNotificationIntent(notification);
        }
    }

    private void displayNotificationIntent(Notification notification) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("menuFragment", "CotacoesFragment");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(Html.fromHtml(notification.title));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(notification.content)));
        builder.setContentText(Html.fromHtml(notification.content));
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pecbrlogo));
        builder.setSmallIcon(R.drawable.pecbrlogo);
        builder.setDefaults(android.app.Notification.DEFAULT_LIGHTS);
        builder.setContentIntent(pendingIntent);
        builder.setColor(getColor(R.color.grey_80));
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(android.app.Notification.PRIORITY_HIGH);
        }

        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(notification.content)));
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int unique_id = (int) System.currentTimeMillis();
        notificationManager.notify(unique_id, builder.build());
        try {
            RingtoneManager.getRingtone(this, Uri.parse("content://settings/system/notification_sound")).play();
        } catch (Exception e) {
        }
    }

}
