package com.smartminds.lockit.locklib.db;

import android.graphics.drawable.Drawable;

import com.smartminds.lockit.locklib.BasicLockInfo;

/**
 * Created by santhosh on 16/6/15.
 */
public class BasicLockInfoImpl implements BasicLockInfo {

    private boolean isLocked;
    private String label;
    private String description;
    private AdvancedAppLock.Type type;

    BasicLockInfoImpl(boolean isLocked, String label, String description, AdvancedAppLock.Type type) {
        this.isLocked = isLocked;
        this.label = label;
        this.description = description;
        this.type = type;
    }

    @Override
    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public void setLock(boolean enable) {
        this.isLocked = enable;
    }

    @Override
    public Drawable getIcon() {
        return null;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getLockDescription() {
        return description;
    }

    public AdvancedAppLock.Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BasicLockInfoImpl) {
            return type == ((BasicLockInfoImpl) o).getType();
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
