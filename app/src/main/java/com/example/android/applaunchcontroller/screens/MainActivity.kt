package com.example.android.applaunchcontroller.screens

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import com.example.android.applaunchcontroller.R
import com.example.android.applaunchcontroller.base.BaseActivity
import com.example.android.applaunchcontroller.entities.InstalledApp
import com.example.android.applaunchcontroller.extensions.getAppLabel
import com.example.android.applaunchcontroller.extensions.getDrawableFromResId
import com.example.android.applaunchcontroller.extensions.showMessageNotification
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), InstalledAppsAdapter.ItemClickListener {

    private val greenRectDrawable: Drawable? by lazy {
        getDrawableFromResId(R.drawable.rectangle_rounded_green)
    }

    private val redRectDrawable: Drawable? by lazy {
        getDrawableFromResId(R.drawable.rectangle_rounded_red)
    }

    private val installedAppsAdapter: InstalledAppsAdapter by lazy {
        InstalledAppsAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRecyclerView()
        updateDontDisturbButton(userPrefs.isDontDisturbModeStarted)
        loadInstalledApps()

        btn_dont_disturb.setOnClickListener {
            if (userPrefs.isDontDisturbModeStarted) {
                updateDontDisturbStatus(false)
                showMessageNotification(getString(R.string.notification_stop_msg))
            } else {
                if (isAccessibilityEnabled) {
                    updateDontDisturbStatus(true)
                    showMessageNotification(getString(R.string.notification_start_msg))
                } else {
                    startWindowChangeDetectionService()
                }
            }
        }
    }

    private fun initRecyclerView() {
        recyclerview.adapter = installedAppsAdapter
        recyclerview.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
    }

    private fun loadInstalledApps() {
        val applicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val installedAppsList = applicationInfoList?.filter {
            !isSystemApp(it) && !isThisApp(it)
        }?.map<ApplicationInfo, InstalledApp> {
            InstalledApp(it.packageName,
                    getAppLabel(it),
                    userPrefs.isInBlackList(it.packageName))
        }
        installedAppsAdapter.update(installedAppsList)
        installedAppsAdapter.setItemClickListener(this@MainActivity)
    }

    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    private fun isThisApp(applicationInfo: ApplicationInfo) = packageName == applicationInfo.packageName

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun onItemClick(installedApp: InstalledApp, position: Int) {
        if (installedApp.isInBlackList) {
            userPrefs.removePackageNameFromBlackList(installedApp.packageName)
        } else {
            userPrefs.addPackageNameToBlackList(installedApp.packageName)
        }
        installedApp.isInBlackList = !installedApp.isInBlackList
        installedAppsAdapter.update(installedApp, position)
    }

    private fun updateDontDisturbStatus(isEnabled: Boolean) {
        userPrefs.isDontDisturbModeStarted = isEnabled
        updateDontDisturbButton(isEnabled)
    }

    private fun updateDontDisturbButton(isDontDisturbStarted: Boolean) {
        btn_dont_disturb.background = if (isDontDisturbStarted) redRectDrawable else greenRectDrawable
        dont_disturb_btn_tv.setText(if (isDontDisturbStarted) R.string.stop else R.string.start)
    }

    private fun startWindowChangeDetectionService() {
        val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivityForResult(intent, REQUEST_RESULT_ACCESSIBILITY_SERVICE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_RESULT_ACCESSIBILITY_SERVICE) {
            if (isAccessibilityEnabled) {
                updateDontDisturbStatus(true)
                showMessageNotification(getString(R.string.notification_start_msg))
            } else {
                showAccesibilityEnableAlertDialog()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showAccesibilityEnableAlertDialog() {
        val builder = AlertDialog.Builder(this@MainActivity)
                .setTitle(R.string.warning)
                .setMessage(R.string.msg_accessibility)
                .setPositiveButton(android.R.string.ok) { _, _ -> startWindowChangeDetectionService() }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
        builder.create().show()
    }

    companion object {
        const val REQUEST_RESULT_ACCESSIBILITY_SERVICE = 1313
    }
}
