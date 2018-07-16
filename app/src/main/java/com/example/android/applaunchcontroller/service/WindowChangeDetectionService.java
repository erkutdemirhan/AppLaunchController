package com.example.android.applaunchcontroller.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.example.android.applaunchcontroller.AppLaunchController;
import com.example.android.applaunchcontroller.screens.MainActivity;
import com.example.android.applaunchcontroller.utils.UserPreferences;


/**
 *  An {@link AccessibilityService} class to listen for changes on the foreground and retrieve package name of
 *  the application showing up the screen. The package name is queried for being in the black list. If so,
 *  a notification is being shown and the {@link MainActivity} is started.
 */
public class WindowChangeDetectionService extends AccessibilityService {

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
                        getApp().showBlackListNotification(packageName);
                        gotoMainScreen();
                    }
                }
            }
        }
    }

    private UserPreferences getUserPrefs() {
        return getApp().getUserPreferences();
    }

    private AppLaunchController getApp() {
        return ((AppLaunchController) getApplication());
    }

    private void gotoMainScreen() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onInterrupt() {}
}
