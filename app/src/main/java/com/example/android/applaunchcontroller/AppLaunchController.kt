package com.example.android.applaunchcontroller

import android.app.Application
import com.example.android.applaunchcontroller.extensions.showBlackListNotification
import com.example.android.applaunchcontroller.extensions.showMessageNotification
import com.example.android.applaunchcontroller.utils.UserPreferences


class AppLaunchController : Application() {

    val userPreferences: UserPreferences by lazy {
        UserPreferences(applicationContext)
    }

    fun showMessageNotification(message: String) {
        applicationContext.showMessageNotification(message)
    }

    fun showBlackListNotification(packageName: String) {
        applicationContext.showBlackListNotification(packageName)
    }

}
