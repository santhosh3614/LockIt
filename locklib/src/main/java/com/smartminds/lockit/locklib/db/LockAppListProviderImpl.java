package com.smartminds.lockit.locklib.db;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.appsforbb.common.appbase.AppBase;
import com.smartminds.lockit.locklib.BasicLockInfo;
import com.smartminds.lockit.locklib.LockAppInfo;
import com.smartminds.lockit.locklib.LockAppListProvider;
import com.smartminds.lockit.locklib.UserProfile;
import com.smartminds.lockit.locklib.appinfo.AppInfoProvider;
import com.smartminds.lockit.locklib.appinfo.LaunchableAppInfo;
import com.smartminds.lockit.locklib.common.lockscreen.AppLockSearchableAdapter;
import com.smartminds.lockit.locklib.services.AppLockService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        if(basicLockInfo instanceof BasicLockInfoImpl){
            long userProfileId=((LockItProfile)userProfile).getId();
            db.getAdvancedLockAppInfoTable().updateBasicAppLock(userProfileId,locked,
                    ((BasicLockInfoImpl) basicLockInfo).getType());
        }else{
            String packageName=((LockAppInfo)basicLockInfo).getPackageName();
            if (locked) {
                db.getAppInfoTable().insertLockedApp(packageName, userProfile);
            } else {
                db.getAppInfoTable().deleteLockAppInfo(packageName, userProfile);
            }
            Intent intent = new Intent(AppLockService.ACTION_LOCK_UPDATED);
            intent.putExtra(AppLockService.EXTRA_LOCK_PKG, packageName);
            intent.putExtra(AppLockService.EXTRA_LOCK_STATUS, locked);
            LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(intent);
        }
    }

    @Override
    public LockAppInfo[] getAppListInfo(UserProfile userProfile, LockAppListProvider.Filter filter,final SortOrder order) {
        LaunchableAppInfo[] launchableAppInfo = AppInfoProvider.getAllLaunchableApps(true, true);
        List<String> lockedPkgs = getLockedPackages(userProfile);
        List<BasicLockInfo> lockAppInfos = new ArrayList<BasicLockInfo>();
        for (int i = 0; i < launchableAppInfo.length; i++) {
            LaunchableAppInfo launchableAppInfo1 = launchableAppInfo[i];
            boolean islocked = lockedPkgs.contains(launchableAppInfo1.getPackageName());
            if (filter == Filter.ALL || (filter == Filter.LOCKED && islocked) || (filter == Filter.UNLOCKED && !islocked))
                lockAppInfos.add(new LockAppInfoImpl(launchableAppInfo1, islocked));
        }
        Collections.sort(lockAppInfos, new LockComparator(order));
        return lockAppInfos.toArray(new LockAppInfo[lockAppInfos.size()]);
    }

    @Override
    public AppLockSearchableAdapter getAdapter(Filter filter, SortOrder sortOrder, UserProfile userProfile) {
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

        private SortOrder sortOrder;
        private int sign;

        LockComparator(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
            sign = sortOrder == SortOrder.LOCKED_FIRST ? 1 : -1;
        }

        @Override
        public int compare(BasicLockInfo lhs, BasicLockInfo rhs) {
            if (sortOrder == SortOrder.NAME) {
                return lhs.getLabel().compareToIgnoreCase(rhs.getLabel());
            }
            if (lhs.isLocked() == rhs.isLocked()) {
                return lhs.getLabel().compareToIgnoreCase(rhs.getLabel());
            }
            return lhs.isLocked() ? sign : -sign;
        }

    }
}
