package com.smartminds.lockit.locklib;

public interface LockAppInfo extends BasicLockInfo{

    public String getPackageName();

    public String getActivityName() ;

    public boolean isSystemApp();

}
