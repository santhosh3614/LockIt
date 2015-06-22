package com.smartminds.lockit.locklib.db;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.appsforbb.common.appbase.AppBase;
import com.smartminds.lockit.locklib.LockAppInfo;
import com.smartminds.lockit.locklib.R;
import com.smartminds.lockit.locklib.appinfo.LaunchableAppInfo;


public class LockAppInfoImpl implements LockAppInfo {
    private LaunchableAppInfo info;
    private boolean isLocked;
    private String description;

    LockAppInfoImpl(LaunchableAppInfo launchableAppInfo, boolean isLocked) {
        this(launchableAppInfo, isLocked, null);
    }

    LockAppInfoImpl(LaunchableAppInfo launchableAppInfo, boolean isLocked, String description) {
        this.info = launchableAppInfo;
        this.isLocked = isLocked;
        this.description = description;
    }

    @Override
    public String getPackageName() {
        return info.getPackageName();
    }

    @Override
    public String getActivityName() {
        return info.getActivityName();
    }

    @Override
    public String getLabel() {
        return info.getLabel();
    }

    @Override
    public String getLockDescription() {
        if (description != null) {
            return description;
        }
        Context context = AppBase.getAppContext();
        return context.getString(isSystemApp() ? R.string.system_app : R.string.thirdparty_app);
    }

    @Override
    public Drawable getIcon() {
        return info.getIcon();
    }

    @Override
    public boolean isSystemApp() {
        return info.isSystemApp();
    }

    @Override
    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public void setLock(boolean enable) {
        this.isLocked = enable;
    }

    void setLockDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LockAppInfoImpl) {
            LockAppInfoImpl olock = (LockAppInfoImpl) o;
            return info.equals(olock.info);
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return info.hashCode();
    }

    @Override
    public String toString() {
        return info.toString();
    }
}