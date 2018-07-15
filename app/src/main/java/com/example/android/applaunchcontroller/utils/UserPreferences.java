package com.example.android.applaunchcontroller.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;

public class UserPreferences {

    private static final String PREFS_KEY = "key.user.prefs";
    private static final String DONT_DISTURB_MODE_STARTED_KEY = "key.dont.disturb.mode.started";
    private static final String BLACKLISTED_PACKAGES_KEY = "key.blacklisted.packages";

    private static final Type STRING_SET_TYPE = new TypeToken<HashSet<String>>() {}.getType();

    private Gson gson;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private HashSet<String> blacklistedPackagesSet;

    public UserPreferences(Context context) {
        gson = new Gson();
        prefs  = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public boolean isDontDisturbModeStarted() {
        return prefs.getBoolean(DONT_DISTURB_MODE_STARTED_KEY, false);
    }

    public void setDontDisturbModeStarted(final boolean isStarted) {
        editor.putBoolean(DONT_DISTURB_MODE_STARTED_KEY, isStarted).apply();
    }

    public void addPackageNameToBlackList(final String packageName) {
        getBlackListedPackageNames().add(packageName);
        saveStringSetToSharedPrefs(getBlackListedPackageNames());
    }

    public void removePackageNameFromBlackList(final String packageName) {
        getBlackListedPackageNames().remove(packageName);
        saveStringSetToSharedPrefs(getBlackListedPackageNames());
    }

    public boolean isInBlackList(final String packageName) {
        return getBlackListedPackageNames().contains(packageName);
    }

    private HashSet<String> getBlackListedPackageNames() {
        if (blacklistedPackagesSet == null) {
            blacklistedPackagesSet = loadStringSetFromSharedPrefs();
        }
        return blacklistedPackagesSet;
    }

    private void saveStringSetToSharedPrefs(final HashSet<String> stringSet) {
        String stringSetJson = gson.toJson(stringSet);
        editor.putString(BLACKLISTED_PACKAGES_KEY, stringSetJson).apply();
    }

    private HashSet<String> loadStringSetFromSharedPrefs() {
        String stringSetJson = prefs.getString(BLACKLISTED_PACKAGES_KEY, "");
        if (!stringSetJson.isEmpty()) {
            return gson.fromJson(stringSetJson, STRING_SET_TYPE);
        } else {
            return new HashSet<>();
        }
    }
}
