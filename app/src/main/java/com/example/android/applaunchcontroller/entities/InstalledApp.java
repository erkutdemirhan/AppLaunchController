package com.example.android.applaunchcontroller.entities;

public class InstalledApp {

    private String packageName;
    private String name;
    private boolean isInBlackList;

    public InstalledApp(String packageName, String name, boolean isInBlackList) {
        this.packageName = packageName;
        this.name = name;
        this.isInBlackList = isInBlackList;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public boolean isInBlackList() {
        return isInBlackList;
    }

    public void setInBlackList(boolean inBlackList) {
        isInBlackList = inBlackList;
    }
}
