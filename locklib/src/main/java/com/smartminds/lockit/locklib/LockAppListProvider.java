package com.smartminds.lockit.locklib;


import com.smartminds.lockit.locklib.common.lockscreen.AppLockSearchableAdapter;
import com.smartminds.lockit.locklib.db.AdvancedAppLock;

import java.util.List;

public interface LockAppListProvider {

    public enum Filter {
        ALL, LOCKED, UNLOCKED
    }

    public enum SortOrder {
        NAME, LOCKED_FIRST, UNLOCKED_FIRST
    }

    public abstract boolean isAppLocked(String packageName, long userProfileId);

    public abstract void lockApp(BasicLockInfo basicLockInfo, boolean locked, UserProfile userProfile);

    public abstract AppLockSearchableAdapter getAdapter(Filter filter, SortOrder sortOrder, UserProfile userProfile);

    public LockAppInfo[] getAppListInfo(UserProfile userProfile, Filter filter, final SortOrder order);

    public List<String> getLockedPackages(UserProfile userProfile);

    public AdvancedAppLock getAdvancedLock(UserProfile userProfile);

}