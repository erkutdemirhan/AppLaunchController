package com.example.android.applaunchcontroller.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.android.applaunchcontroller.AppLaunchController;
import com.example.android.applaunchcontroller.utils.UserPreferences;

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

}
