package com.example.android.applaunchcontroller.extensions

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import com.example.android.applaunchcontroller.R

const val NOTIFICATION_CHANNEL_ID = "com.applaunchcontroller.channel.id"

fun Context.getAppLabel(packageName: String): String {
    val appInfo: ApplicationInfo? = try {
        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
    return getAppLabel(appInfo)
}

fun Context.getAppLabel(appInfo:ApplicationInfo?): String {
    return if (appInfo != null) packageManager.getApplicationLabel(appInfo) as String
    else getString(R.string.this_app)
}

fun Context.getDrawableFromResId(drawableResId:Int):Drawable? {
    return ContextCompat.getDrawable(this, drawableResId)
}

fun Context.showNotification(titleStr: String, messageStr: String, notificationId: Int) {
    val notificationBuilder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(titleStr)
            .setContentText(messageStr)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
    notificationManagerCompat.notify(notificationId, notificationBuilder.build())
}

fun Context.showMessageNotification(messageStr: String) {
    showNotification(getString(R.string.notification_title), messageStr, packageName.hashCode())
}

fun Context.showBlackListNotification(blackListedAppPackageName: String) {
    showNotification(getString(R.string.notification_title),
            getString(R.string.notification_blacklist_msg, getAppLabel(packageName)),
            blackListedAppPackageName.hashCode())
}