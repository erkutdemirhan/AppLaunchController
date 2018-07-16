package com.example.android.applaunchcontroller;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.android.applaunchcontroller.utils.UserPreferences;


public class AppLaunchController extends Application {

    private static final String CHANNEL_ID = "com.applaunchcontroller.channel.id";

    private UserPreferences userPreferences;

    public UserPreferences getUserPreferences() {
        if (userPreferences == null) {
            userPreferences = new UserPreferences(getApplicationContext());
        }

        return userPreferences;
    }

    public void showMessageNotification(final String message) {
        showNotification(message, getString(R.string.notification_title), getPackageName().hashCode());
    }

    public void showBlackListNotification(final String packageName) {
        String appLabel = getAppLabel(packageName);
        appLabel = appLabel.isEmpty() ? "This app":appLabel;

        showNotification(getString(R.string.notification_blacklist_msg, appLabel),
                getString(R.string.notification_title),
                packageName.hashCode());
    }

    private String getAppLabel(final String packageName) {
        PackageManager pm = getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException ignored) {}
        return (appInfo != null) ? (String) pm.getApplicationLabel(appInfo) : "";
    }

    private void showNotification(final String messageStr, final String titleStr, final int notificationId) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(titleStr)
                .setContentText(messageStr)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(notificationId, notificationBuilder.build());
    }
}
