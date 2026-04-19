package com.example.applock;

import android.graphics.drawable.Drawable;

public class AppModel {

    String name;
    String packageName;
    Drawable icon;
    boolean isLocked;

    public AppModel(String name, String packageName, Drawable icon, boolean isLocked) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        this.isLocked = isLocked;
    }
}