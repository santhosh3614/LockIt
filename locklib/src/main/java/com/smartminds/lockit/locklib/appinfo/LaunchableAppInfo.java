package com.smartminds.lockit.locklib.appinfo;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.appsforbb.common.appbase.AppBase;
import com.smartminds.lockit.locklib.others.Weak;

public class LaunchableAppInfo {

    private static final PackageManager pm = AppBase.getAppContext().getPackageManager();

    private ActivityInfo info;
    private String packageName;
    private String activityName;
    private String label;
    private Weak<Drawable> icon;
    private boolean isSystemApp;

    LaunchableAppInfo(final ActivityInfo info) {
        this.info = info;
        this.activityName = info.name;
        this.packageName = info.packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getLabel() {
        if (label == null) {
            label = info.loadLabel(pm).toString();
        }
        return label;
    }

    public Drawable getIcon() {
        if (icon == null) {
            icon = new Weak<Drawable>() {
                @Override
                protected Drawable load() {
                    return info.loadIcon(pm);
                }
            };
        }
        return icon.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LaunchableAppInfo) {
            return getPackageName().equals(((LaunchableAppInfo) obj).getPackageName());
        }
        return super.equals(obj);
    }

    public boolean isSystemApp() {
        return packageName.equals("com.android");
    }

    @Override
    public String toString() {
        return getLabel();
    }
}