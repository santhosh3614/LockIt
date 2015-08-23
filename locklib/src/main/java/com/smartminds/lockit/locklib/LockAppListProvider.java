package com.smartminds.lockit.locklib;


import com.smartminds.lockit.locklib.common.lockscreen.AppLockSearchableAdapter;
import com.smartminds.lockit.locklib.common.lockscreen.Filters;
import com.smartminds.lockit.locklib.db.AdvancedAppLock;

import java.util.List;

public interface LockAppListProvider {

    public abstract boolean isAppLocked(String packageName, long userProfileId);

    public abstract void lockApp(BasicLockInfo basicLockInfo, boolean locked, UserProfile userProfile);

    public abstract AppLockSearchableAdapter getAdapter(Filters.Filter filter, Filters.SortOrder sortOrder, UserProfile userProfile);

    public LockAppInfo[] getAppListInfo(UserProfile userProfile, Filters.Filter filter, final Filters.SortOrder order);

    public List<String> getLockedPackages(UserProfile userProfile);

    public AdvancedAppLock getAdvancedLock(UserProfile userProfile);

}