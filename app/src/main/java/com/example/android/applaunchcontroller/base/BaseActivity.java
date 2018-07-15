package com.example.android.applaunchcontroller.base;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import com.example.android.applaunchcontroller.AppLaunchController;
import com.example.android.applaunchcontroller.utils.UserPreferences;

import java.util.List;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(BaseActivity.this);
    }

    protected AppLaunchController getApp() {
        return ((AppLaunchController) getApplication());
    }

    protected UserPreferences getUserPrefs() {
        return getApp().getUserPreferences();
    }

    protected abstract int getLayoutId();

    protected boolean isAccessibilityEnabled() {
        AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        boolean isEnabled = false;
        for (AccessibilityServiceInfo runningService : runningServices) {
            if (runningService.getId().contains(getApplicationContext().getPackageName())) {
                isEnabled = true;
                break;
            }
        }
        return isEnabled;
    }
}
