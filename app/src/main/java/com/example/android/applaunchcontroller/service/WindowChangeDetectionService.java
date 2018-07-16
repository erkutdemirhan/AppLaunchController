package com.example.android.applaunchcontroller.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.accessibility.AccessibilityEvent;

import com.example.android.applaunchcontroller.AppLaunchController;
import com.example.android.applaunchcontroller.R;
import com.example.android.applaunchcontroller.screens.MainActivity;
import com.example.android.applaunchcontroller.utils.UserPreferences;


public class WindowChangeDetectionService extends AccessibilityService {

    private static final String CHANNEL_ID = "com.applaunchcontroller.channel.id";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (getUserPrefs().isDontDisturbModeStarted()) {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (event.getPackageName() != null) {
                    final String packageName = event.getPackageName().toString();
                    if (getUserPrefs().isInBlackList(packageName)) {
                        showNotification(packageName);
                        gotoMainScreen();
                    }
                }
            }

        }
    }

    private void showNotification(final String packageName) {
        String appLabel = getAppLabel(packageName);
        appLabel = appLabel.isEmpty() ? "This app":appLabel;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_message, appLabel))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(packageName.hashCode(), notificationBuilder.build());
    }

    private String getAppLabel(final String packageName) {
        PackageManager pm = getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException ignored) {}
        return (appInfo != null) ? (String) pm.getApplicationLabel(appInfo) : "";
    }

    private UserPreferences getUserPrefs() {
        return ((AppLaunchController) getApplication()).getUserPreferences();
    }

    private void gotoMainScreen() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onInterrupt() {}
}
