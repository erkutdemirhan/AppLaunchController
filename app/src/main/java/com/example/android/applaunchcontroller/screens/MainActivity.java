package com.example.android.applaunchcontroller.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.applaunchcontroller.R;
import com.example.android.applaunchcontroller.base.BaseActivity;
import com.example.android.applaunchcontroller.entities.InstalledApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements InstalledAppsAdapter.ItemClickListener {

    public static final int REQUEST_RESULT_ACCESSIBILITY_SERVICE = 1313;

    @BindView(R.id.btn_dont_disturb)
    protected RelativeLayout dontDisturbBtn;

    @BindView(R.id.recyclerview)
    protected RecyclerView installedAppsRecyclerView;

    @BindView(R.id.dont_disturb_btn_tv)
    protected TextView dontDisturbBtnTv;

    @BindDrawable(R.drawable.rectangle_rounded_green)
    protected Drawable greenRectDrawable;

    @BindDrawable(R.drawable.rectangle_rounded_red)
    protected Drawable redRectDrawable;

    private InstalledAppsAdapter installedAppsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRecyclerView();
        updateDontDisturbButton(getUserPrefs().isDontDisturbModeStarted());
        loadInstalledApps();
    }

    private void initRecyclerView() {
        installedAppsAdapter = new InstalledAppsAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        installedAppsRecyclerView.setAdapter(installedAppsAdapter);
        installedAppsRecyclerView.setLayoutManager(llm);
    }

    private void loadInstalledApps() {
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> applicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<InstalledApp> installedAppList = new ArrayList<>();
        if (applicationInfoList != null) {
            for (ApplicationInfo applicationInfo : applicationInfoList) {
                if (!isSystemApp(applicationInfo) && !isThisApp(applicationInfo)) {
                    final boolean isInBlackList = getUserPrefs().isInBlackList(applicationInfo.packageName);
                    installedAppList.add(new InstalledApp(applicationInfo.packageName,
                            (String) packageManager.getApplicationLabel(applicationInfo),
                            isInBlackList));
                }
            }
        }
        installedAppsAdapter.update(installedAppList);
        installedAppsAdapter.setItemClickListener(MainActivity.this);
    }

    private boolean isSystemApp(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    private boolean isThisApp(ApplicationInfo applicationInfo) {
        return getPackageName().equals(applicationInfo.packageName);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onItemClick(InstalledApp installedApp, int position) {
        if (installedApp.isInBlackList()) {
            getUserPrefs().removePackageNameFromBlackList(installedApp.getPackageName());
        } else {
            getUserPrefs().addPackageNameToBlackList(installedApp.getPackageName());
        }
        installedApp.setInBlackList(!installedApp.isInBlackList());
        installedAppsAdapter.update(installedApp, position);
    }

    @OnClick(R.id.btn_dont_disturb)
    protected void onDontDisturbButtonPressed() {
        final boolean isDontDisturbModeStarted = getUserPrefs().isDontDisturbModeStarted();
        if (isDontDisturbModeStarted) {
            updateDontDisturbStatus(false);
            getApp().showMessageNotification(getString(R.string.notification_stop_msg));
        } else {
            if (isAccessibilityEnabled()) {
                updateDontDisturbStatus(true);
                getApp().showMessageNotification(getString(R.string.notification_start_msg));
            } else {
                startWindowChangeDetectionService();
            }
        }
    }

    private void updateDontDisturbStatus(final boolean isEnabled) {
        getUserPrefs().setDontDisturbModeStarted(isEnabled);
        updateDontDisturbButton(isEnabled);
    }

    private void updateDontDisturbButton(final boolean isDontDisturbStarted) {
        dontDisturbBtn.setBackground(isDontDisturbStarted ? redRectDrawable:greenRectDrawable);
        dontDisturbBtnTv.setText(isDontDisturbStarted ? R.string.stop:R.string.start);
    }

    private void startWindowChangeDetectionService() {
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, REQUEST_RESULT_ACCESSIBILITY_SERVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RESULT_ACCESSIBILITY_SERVICE) {
            if (isAccessibilityEnabled()) {
                updateDontDisturbStatus(true);
                getApp().showMessageNotification(getString(R.string.notification_start_msg));
            } else {
                showAccesibilityEnableAlertDialog();
            }
        }
    }

    private void showAccesibilityEnableAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.warning)
                .setMessage(R.string.msg_accessibility)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startWindowChangeDetectionService();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
        builder.create().show();
    }
}
