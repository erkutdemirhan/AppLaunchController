package com.example.android.applaunchcontroller;

import android.app.Application;

import com.example.android.applaunchcontroller.utils.UserPreferences;


public class AppLaunchController extends Application {

    private UserPreferences userPreferences;

    public UserPreferences getUserPreferences() {
        if (userPreferences == null) {
            userPreferences = new UserPreferences(getApplicationContext());
        }

        return userPreferences;
    }
}
