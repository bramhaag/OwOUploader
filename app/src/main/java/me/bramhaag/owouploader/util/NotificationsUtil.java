/*
 * OwO Uploader
 * Copyright (C) 2021
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.bramhaag.owouploader.util;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.util.Pair;
import me.bramhaag.owouploader.R;

/**
 * Util for creating notifications.
 */
public class NotificationsUtil {

    public static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "me.bramhaag.owouploader.app";
    private static final String NOTIFICATION_CHANNEL_NAME = "me.bramhaag.owouploader.app";

    /**
     * Get the notification's details.
     *
     * @param context the context
     * @return the notification's id and the notification
     */
    @NonNull
    public static Pair<Integer, Notification> getNotification(@NonNull Context context) {
        var notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(notificationManager);

        var notification = createNotification(context);
        notificationManager.notify(NOTIFICATION_ID, notification);
        return new Pair<>(NOTIFICATION_ID, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(@NonNull NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW);

        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
    }

    @NonNull
    private static Notification createNotification(@NonNull Context context) {
        return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("Recording").setOngoing(true).setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(Notification.PRIORITY_LOW).setShowWhen(true)
                .build();
    }
}
