package tk.talcharnes.unborify.Utilities;

import android.app.Activity;
import android.app.NotificationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;

import tk.talcharnes.unborify.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by khuramchaudhry on 2/28/18.
 *
 */

public class NotificatonConstants {

    public static final String UPLOAD_ID = "upload_notification";

    public static void sendNotification(Activity activity, String title, String message,
                                        String channelId) {
        if(activity != null) {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            android.app.Notification notification = new NotificationCompat
                    .Builder(activity, channelId)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setSound(defaultSoundUri)
                    .build();

            NotificationManager notificationManager = (NotificationManager)
                    activity.getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                int SERVER_DATA_RECEIVED = 0;
                notificationManager.notify(SERVER_DATA_RECEIVED, notification);
                NavUtils.navigateUpFromSameTask(activity);
            }
        }
    }
}
