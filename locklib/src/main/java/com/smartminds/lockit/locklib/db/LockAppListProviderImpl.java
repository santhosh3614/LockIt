package com.smartminds.lockit.locklib.db;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.smartminds.lockit.locklib.BasicLockInfo;
import com.smartminds.lockit.locklib.LockAppInfo;
import com.smartminds.lockit.locklib.LockAppListProvider;
import com.smartminds.lockit.locklib.UserProfile;
import com.smartminds.lockit.locklib.appinfo.AppInfoProvider;
import com.smartminds.lockit.locklib.appinfo.LaunchableAppInfo;
import com.smartminds.lockit.locklib.common.lockscreen.AppLockSearchableAdapter;
import com.smartminds.lockit.locklib.common.lockscreen.Filters;
import com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedLocks.AdvancedAppBasicLock;
import com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedSwitchLocks.AdvancedAppSwitchBasicLock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.appsforbb.common.appbase.AppBase.getAppContext;
import static com.smartminds.lockit.locklib.common.lockscreen.Filters.Filter.ALL;
import static com.smartminds.lockit.locklib.common.lockscreen.Filters.Filter.LOCKED;
import static com.smartminds.lockit.locklib.common.lockscreen.Filters.Filter.UNLOCKED;
import static com.smartminds.lockit.locklib.common.lockscreen.Filters.SortOrder.LOCKED_FIRST;
import static com.smartminds.lockit.locklib.common.lockscreen.Filters.SortOrder.NAME;
import static com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedLocks.Type;
import static com.smartminds.lockit.locklib.services.BasicLockService.ACTION_APPS_INSTALL_UNINSTALL_CHANGE;
import static com.smartminds.lockit.locklib.services.BasicLockService.ACTION_LOCK_TASK_MANGER_CHANGED;
import static com.smartminds.lockit.locklib.services.BasicLockService.ACTION_LOCK_TELEPHONY_CALLS_CHANGE;
import static com.smartminds.lockit.locklib.services.BasicLockService.ACTION_LOCK_UPDATED;
import static com.smartminds.lockit.locklib.services.BasicLockService.EXTRA_ADVANCEDAPPLOCK_STATUS;
import static com.smartminds.lockit.locklib.services.BasicLockService.EXTRA_LOCK_PKG;
import static com.smartminds.lockit.locklib.services.BasicLockService.EXTRA_LOCK_STATUS;

/**
 * Created by santhosh on 14/6/15.
 */
public class LockAppListProviderImpl implements LockAppListProvider {

    @Override
    public boolean isAppLocked(String packageName, long userProfileId) {
        Db db = Db.getInstance();
        return db.getAppInfoTable().getSingleRecord(packageName, userProfileId) != null;
    }

    @Override
    public void lockApp(BasicLockInfo basicLockInfo, boolean locked, UserProfile userProfile) {
        Db db = Db.getInstance();
        if (basicLockInfo instanceof LockAppInfoImpl) {
            String packageName = ((LockAppInfo) basicLockInfo).getPackageName();
            if (locked) {
                db.getAppInfoTable().insertLockedApp(packageName, userProfile);
            } else {
                db.getAppInfoTable().deleteLockAppInfo(packageName, userProfile);
            }
            Intent intent = new Intent(ACTION_LOCK_UPDATED);
            intent.putExtra(EXTRA_LOCK_PKG, packageName);
            intent.putExtra(EXTRA_LOCK_STATUS, locked);
            LocalBroadcastManager.getInstance(getAppContext()).sendBroadcast(intent);
        } else if (basicLockInfo instanceof AdvancedAppBasicLock) {
            AdvancedAppBasicLock advancedAppBasicLock = (AdvancedAppBasicLock) basicLockInfo;
            long userProfileId = ((LockItProfile) userProfile).getId();
            db.getAdvancedLockAppInfoTable().updateBasicAppLock(userProfileId, locked, basicLockInfo);
            Intent intent = new Intent();
            intent.putExtra(EXTRA_ADVANCEDAPPLOCK_STATUS, locked);
            Type type = advancedAppBasicLock.getType();
            switch (type) {
                case RECENTASK:
                    intent.setAction(ACTION_APPS_INSTALL_UNINSTALL_CHANGE);
                    break;
                case TASKMANAGER:
                    intent.setAction(ACTION_LOCK_TASK_MANGER_CHANGED);
                    break;
                case UN_INSTALL:
                    intent.setAction(ACTION_APPS_INSTALL_UNINSTALL_CHANGE);
                    break;
                case INCOMMINGCALL:
                    intent.setAction(ACTION_LOCK_TELEPHONY_CALLS_CHANGE);
                    break;
            }
            LocalBroadcastManager.getInstance(getAppContext()).sendBroadcast(intent);
        } else if (basicLockInfo instanceof AdvancedAppSwitchBasicLock) {
            long userProfileId = ((LockItProfile) userProfile).getId();
            db.getAdvancedLockAppInfoTable().updateBasicAppLock(userProfileId, locked, basicLockInfo);
        }
    }

    @Override
    public LockAppInfo[] getAppListInfo(UserProfile userProfile, Filters.Filter filter, final Filters.SortOrder order) {
        LaunchableAppInfo[] launchableAppInfo = AppInfoProvider.getAllLaunchableApps(true, true);
        List<String> lockedPkgs = getLockedPackages(userProfile);
        List<BasicLockInfo> lockAppInfos = new ArrayList<BasicLockInfo>();
        for (int i = 0; i < launchableAppInfo.length; i++) {
            LaunchableAppInfo launchableAppInfo1 = launchableAppInfo[i];
            boolean islocked = lockedPkgs.contains(launchableAppInfo1.getPackageName());
            if (filter == ALL || (filter == LOCKED && islocked) || (filter == UNLOCKED && !islocked))
                lockAppInfos.add(new LockAppInfoImpl(launchableAppInfo1, islocked));
        }
        Collections.sort(lockAppInfos, new LockComparator(order));
        return lockAppInfos.toArray(new LockAppInfo[lockAppInfos.size()]);
    }

    @Override
    public AppLockSearchableAdapter getAdapter(Filters.Filter filter, Filters.SortOrder sortOrder, UserProfile userProfile) {
        return new AppLockSearchableApaterImpl(userProfile, filter, sortOrder);
    }

    public List<String> getLockedPackages(UserProfile userProfile) {
        return Db.getInstance().getAppInfoTable().getAllLockedPkgs(userProfile);
    }

    @Override
    public AdvancedAppLock getAdvancedLock(UserProfile userProfile) {
        return Db.getInstance().getAdvancedLockAppInfoTable().getAdvancedAppLock(((LockItProfile) userProfile).getId());
    }

    static class LockComparator implements Comparator<BasicLockInfo> {

        private Filters.SortOrder sortOrder;
        private int sign;

        LockComparator(Filters.SortOrder sortOrder) {
            this.sortOrder = sortOrder;
            sign = sortOrder == LOCKED_FIRST ? 1 : -1;
        }
        @Override
        public int compare(BasicLockInfo lhs, BasicLockInfo rhs) {
            if (sortOrder == NAME) {
                return lhs.getLabel().compareToIgnoreCase(rhs.getLabel());
            }
            if (lhs.isLocked() == rhs.isLocked()) {
                return lhs.getLabel().compareToIgnoreCase(rhs.getLabel());
            }
            return lhs.isLocked() ? sign : -sign;
        }

    }
}
