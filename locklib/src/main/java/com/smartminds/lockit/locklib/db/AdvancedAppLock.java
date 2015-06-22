package com.smartminds.lockit.locklib.db;

import android.content.Context;

import com.appsforbb.common.appbase.AppBase;
import com.smartminds.lockit.locklib.BasicLockInfo;
import com.smartminds.lockit.locklib.R;
import com.smartminds.lockit.locklib.appinfo.AppInfoProvider;
import com.smartminds.lockit.locklib.appinfo.LaunchableAppInfo;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by santhosh on 14/6/15.
 */
public class AdvancedAppLock {


    private static final String SETTINGS_APP_PKG = "com.android.settings";
    private static final String PLAYSTORE_APP_PKG = "com.android.vending";

    static final boolean DEFAULT_LOCK_WIFI = false;
    static final boolean DEFAULT_LOCK_BLUETOOTH= false;
    static final boolean DEFAULT_LOCK_MOBILDATA = false;
    static final boolean DEFAULT_LOCK_AUTOSYNC = false;

    static final boolean DEFAULT_LOCK_SETTINGS = false;
    static final boolean DEFAULT_LOCK_PLAYSTORE = false;
    static final boolean DEFAULT_LOCK_UNINSTALL = false;
    static final boolean DEFAULT_LOCK_RECENTTASK = false;
    static final boolean DEFAULT_LOCK_TASKMANGER = false;
    static final boolean DEFAULT_LOCK_INCOMMING = false;

    private long userProfileId;
    private AdvancedLocks advancedLocks;
    private AdvancedSwitchLocks advancedSwitchLocks;

    static enum Type {
        SETTINGS,PLAYSTORE,RECENTASK,TASKMANAGER,UN_INSTALL,INCOMMINGCALL,
        WIFI,MOBILEDATA,AUTOSYNC,BLUETOOTH
    }

    AdvancedAppLock(long userProfileId,AdvancedLocks advancedLocks, AdvancedSwitchLocks advancedSwitchLocks) {
        this.userProfileId=userProfileId;
        this.advancedLocks = advancedLocks;
        this.advancedSwitchLocks = advancedSwitchLocks;
    }

    AdvancedLocks getAdvancedLock() {
        return advancedLocks;
    }

    AdvancedSwitchLocks getAdvancedSwitchLocks() {
        return advancedSwitchLocks;
    }

    public long getUserProfileId() {
        return userProfileId;
    }


    public static class AdvancedLocks {

        private EnumMap<Type, BasicLockInfo> basicLockInfos = new EnumMap<AdvancedAppLock.Type, BasicLockInfo>(AdvancedAppLock.Type.class);

        public AdvancedLocks(long userProfileId,boolean isUnInstallLocked, boolean isRecentTasksLocked,
                             boolean isTaskManagerLocked, boolean isIncommingCallsLocked) {

            Context appBase = AppBase.getAppContext();

            LockAppInfoImpl lockAppInfo = getAppByPkg(SETTINGS_APP_PKG, userProfileId);
            if (lockAppInfo != null) {
                lockAppInfo.setLockDescription(appBase.getString(R.string.settings_lock_desc));
                basicLockInfos.put(AdvancedAppLock.Type.SETTINGS, lockAppInfo);
            }
            lockAppInfo = getAppByPkg(PLAYSTORE_APP_PKG, userProfileId);
            if (lockAppInfo != null) {
                lockAppInfo.setLockDescription(appBase.getString(R.string.playstore_lock_desc));
                basicLockInfos.put(AdvancedAppLock.Type.PLAYSTORE, lockAppInfo);
            }
            basicLockInfos.put(AdvancedAppLock.Type.UN_INSTALL, new BasicLockInfoImpl(isUnInstallLocked,
                    appBase.getString(R.string.install_uninstall_label),
                    appBase.getString(R.string.install_uninstall_desc), AdvancedAppLock.Type.UN_INSTALL));

            basicLockInfos.put(AdvancedAppLock.Type.RECENTASK, new BasicLockInfoImpl(isRecentTasksLocked,
                    appBase.getString(R.string.recenttasklock_label),
                    appBase.getString(R.string.recenttasklock_desc), AdvancedAppLock.Type.RECENTASK));

            basicLockInfos.put(AdvancedAppLock.Type.TASKMANAGER, new BasicLockInfoImpl(isTaskManagerLocked,
                    appBase.getString(R.string.taskmanager_label),
                    appBase.getString(R.string.taskmanager_desc), AdvancedAppLock.Type.TASKMANAGER));

            basicLockInfos.put(AdvancedAppLock.Type.INCOMMINGCALL, new BasicLockInfoImpl(isIncommingCallsLocked,
                    appBase.getString(R.string.incoming_label),
                    appBase.getString(R.string.incoming_desc), AdvancedAppLock.Type.INCOMMINGCALL));
        }

        private LockAppInfoImpl getAppByPkg(String pkg, long userProfileId) {
            LaunchableAppInfo[] allLaunchableAppsByPkg = AppInfoProvider.getAllLaunchableAppsByPkg(pkg);
            if (allLaunchableAppsByPkg.length > 0) {
                boolean isLocked = Db.getInstance().getAppInfoTable().getSingleRecord(pkg, userProfileId) != null;
                return new LockAppInfoImpl(allLaunchableAppsByPkg[0], isLocked);
            }
            return null;
        }

        public List<BasicLockInfo> getBasicLocks() {
            return new ArrayList<>(basicLockInfos.values());
        }

        public BasicLockInfo getBasicLockInfo(AdvancedAppLock.Type type) {
            return basicLockInfos.get(type);
        }
    }

    public static class AdvancedSwitchLocks {

        private EnumMap<AdvancedAppLock.Type,BasicLockInfo> basicLockInfos=new EnumMap<AdvancedAppLock.Type, BasicLockInfo>(AdvancedAppLock.Type.class);

        public AdvancedSwitchLocks(boolean isWifiLocked, boolean isBluetoothLocked,
                                   boolean isMobileDataLocked, boolean isAutoSyncLocked) {
            Context appBase = AppBase.getAppContext();

            basicLockInfos.put(AdvancedAppLock.Type.WIFI, new BasicLockInfoImpl(isWifiLocked,
                    appBase.getString(R.string.wifi_label),
                    appBase.getString(R.string.install_uninstall_desc), AdvancedAppLock.Type.WIFI));

            basicLockInfos.put(AdvancedAppLock.Type.BLUETOOTH, new BasicLockInfoImpl(isBluetoothLocked,
                    appBase.getString(R.string.bluetooth_label),
                    appBase.getString(R.string.recenttasklock_desc), AdvancedAppLock.Type.BLUETOOTH));

            basicLockInfos.put(AdvancedAppLock.Type.MOBILEDATA, new BasicLockInfoImpl(isMobileDataLocked,
                    appBase.getString(R.string.mobiledata_label),
                    appBase.getString(R.string.taskmanager_desc), AdvancedAppLock.Type.MOBILEDATA));

            basicLockInfos.put(AdvancedAppLock.Type.AUTOSYNC, new BasicLockInfoImpl(isAutoSyncLocked,
                    appBase.getString(R.string.auto_sync_label),
                    appBase.getString(R.string.incoming_desc), AdvancedAppLock.Type.AUTOSYNC));
        }

        public List<BasicLockInfo> getBasicLocks() {
            return new ArrayList<>(basicLockInfos.values());
        }

        public BasicLockInfo getBasicLockInfo(AdvancedAppLock.Type type) {
            return basicLockInfos.get(type);
        }

    }


}
