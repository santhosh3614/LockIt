package com.smartminds.lockit.locklib;

import android.graphics.drawable.Drawable;

/**
 * Created by santhosh on 15/6/15.
 */
public interface BasicLockInfo {

    public boolean isLocked();

    public void setLock(boolean enable);

    public Drawable getIcon();

    public String getLabel();

    public String getLockDescription();



}
