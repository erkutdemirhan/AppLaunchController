package com.example.android.applaunchcontroller.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

import com.example.android.applaunchcontroller.AppLaunchController
import com.example.android.applaunchcontroller.extensions.showBlackListNotification
import com.example.android.applaunchcontroller.screens.MainActivity
import com.example.android.applaunchcontroller.utils.UserPreferences


/**
 * An [AccessibilityService] class to listen for changes on the foreground and retrieve package name of
 * the application showing up the screen. The package name is queried for being in the black list. If so,
 * a notification is being shown and the [MainActivity] is started.
 */
class WindowChangeDetectionService : AccessibilityService() {

    private val userPrefs: UserPreferences by lazy {
        (application as AppLaunchController).userPreferences
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (userPrefs.isDontDisturbModeStarted) {
            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                event.packageName?.toString()?.let { packageName ->
                    if (userPrefs.isInBlackList(packageName)) {
                        showBlackListNotification(packageName)
                        gotoMainScreen()
                    }
                }
            }
        }
    }

    private fun gotoMainScreen() {
        val intent = Intent(baseContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onInterrupt() {}
}
