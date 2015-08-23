package com.smartminds.lockit.locklib.db;

import android.content.Context;
import android.graphics.drawable.Drawable;

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

    private long userProfileId;
    private AdvancedLocks advancedLocks;
    private AdvancedSwitchLocks advancedSwitchLocks;

    AdvancedAppLock(long userProfileId, AdvancedLocks advancedLocks, AdvancedSwitchLocks advancedSwitchLocks) {
        this.userProfileId = userProfileId;
        this.advancedLocks = advancedLocks;
        this.advancedSwitchLocks = advancedSwitchLocks;
    }

    public AdvancedLocks getAdvancedLock() {
        return advancedLocks;
    }

    public AdvancedSwitchLocks getAdvancedSwitchLocks() {
        return advancedSwitchLocks;
    }

    public long getUserProfileId() {
        return userProfileId;
    }


    public static class AdvancedLocks {

        private static final String SETTINGS_APP_PKG = "com.android.settings";
        private static final String PLAYSTORE_APP_PKG = "com.android.vending";

        static final boolean DEFAULT_LOCK_SETTINGS = false;
        static final boolean DEFAULT_LOCK_PLAYSTORE = false;
        static final boolean DEFAULT_LOCK_UNINSTALL = false;
        static final boolean DEFAULT_LOCK_RECENTTASK = false;
        static final boolean DEFAULT_LOCK_TASKMANGER = false;
        static final boolean DEFAULT_LOCK_INCOMMING = false;

        public static enum Type {
            RECENTASK, TASKMANAGER, UN_INSTALL, INCOMMINGCALL,
        }

        private EnumMap<Type, BasicLockInfo> basicLockInfos = new EnumMap<Type, BasicLockInfo>(Type.class);

        public AdvancedLocks(long userProfileId, boolean isUnInstallLocked, boolean isRecentTasksLocked,
                             boolean isTaskManagerLocked, boolean isIncommingCallsLocked) {

            Context appBase = AppBase.getAppContext();
//            LockAppInfoImpl lockAppInfo = getAppByPkg(SETTINGS_APP_PKG, userProfileId);
//            if (lockAppInfo != null) {
//                lockAppInfo.setLockDescription(appBase.getString(R.string.settings_lock_desc));
//                basicLockInfos.put(AdvancedLocks.Type.SETTINGS, lockAppInfo);
//            }
//            lockAppInfo = getAppByPkg(PLAYSTORE_APP_PKG, userProfileId);
//            if (lockAppInfo != null) {
//                lockAppInfo.setLockDescription(appBase.getString(R.string.playstore_lock_desc));
//                basicLockInfos.put(Type.PLAYSTORE, lockAppInfo);
//            }
            basicLockInfos.put(Type.UN_INSTALL, new AdvancedAppBasicLock(isUnInstallLocked,
                    appBase.getString(R.string.install_uninstall_label),
                    appBase.getString(R.string.install_uninstall_desc), Type.UN_INSTALL));

            basicLockInfos.put(Type.RECENTASK, new AdvancedAppBasicLock(isRecentTasksLocked,
                    appBase.getString(R.string.recenttasklock_label),
                    appBase.getString(R.string.recenttasklock_desc), Type.RECENTASK));

            basicLockInfos.put(Type.TASKMANAGER, new AdvancedAppBasicLock(isTaskManagerLocked,
                    appBase.getString(R.string.taskmanager_label),
                    appBase.getString(R.string.taskmanager_desc), Type.TASKMANAGER));

            basicLockInfos.put(Type.INCOMMINGCALL, new AdvancedAppBasicLock(isIncommingCallsLocked,
                    appBase.getString(R.string.incoming_label),
                    appBase.getString(R.string.incoming_desc), Type.INCOMMINGCALL));
        }

//        private LockAppInfoImpl getAppByPkg(String pkg, long userProfileId) {
//            LaunchableAppInfo[] allLaunchableAppsByPkg = AppInfoProvider.getAllLaunchableAppsByPkg(pkg);
//            if (allLaunchableAppsByPkg.length > 0) {
//                boolean isLocked = Db.getInstance().getAppInfoTable().getSingleRecord(pkg, userProfileId) != null;
//                return new LockAppInfoImpl(allLaunchableAppsByPkg[0], isLocked);
//            }
//            return null;
//        }

        public List<BasicLockInfo> getBasicLocks() {
            return new ArrayList<>(basicLockInfos.values());
        }

        public BasicLockInfo getBasicLockInfo(Type type) {
            return basicLockInfos.get(type);
        }

        static class AdvancedAppBasicLock implements BasicLockInfo {

            private boolean isLocked;
            private String label;
            private String description;
            private Type type;

            AdvancedAppBasicLock(boolean isLocked, String label, String description, Type type) {
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

            public Type getType() {
                return type;
            }

            @Override
            public boolean equals(Object o) {
                if (o instanceof AdvancedAppBasicLock) {
                    return type == ((AdvancedAppBasicLock) o).getType();
                }
                return super.equals(o);
            }

            @Override
            public int hashCode() {
                return type.hashCode();
            }
        }

    }

    static class AdvancedSwitchLocks {

        static final boolean DEFAULT_LOCK_WIFI = false;
        static final boolean DEFAULT_LOCK_BLUETOOTH = false;
        static final boolean DEFAULT_LOCK_MOBILDATA = false;
        static final boolean DEFAULT_LOCK_AUTOSYNC = false;

        static enum Type {
            WIFI, MOBILEDATA, AUTOSYNC, BLUETOOTH
        }

        private EnumMap<Type, BasicLockInfo> basicLockInfos = new EnumMap<Type, BasicLockInfo>(AdvancedSwitchLocks.Type.class);

        public AdvancedSwitchLocks(boolean isWifiLocked, boolean isBluetoothLocked,
                                   boolean isMobileDataLocked, boolean isAutoSyncLocked) {
            Context appBase = AppBase.getAppContext();

            basicLockInfos.put(Type.WIFI, new AdvancedAppSwitchBasicLock(isWifiLocked,
                    appBase.getString(R.string.wifi_label),
                    appBase.getString(R.string.install_uninstall_desc), Type.WIFI));

            basicLockInfos.put(Type.BLUETOOTH, new AdvancedAppSwitchBasicLock(isBluetoothLocked,
                    appBase.getString(R.string.bluetooth_label),
                    appBase.getString(R.string.recenttasklock_desc), Type.BLUETOOTH));

            basicLockInfos.put(Type.MOBILEDATA, new AdvancedAppSwitchBasicLock(isMobileDataLocked,
                    appBase.getString(R.string.mobiledata_label),
                    appBase.getString(R.string.taskmanager_desc), Type.MOBILEDATA));

            basicLockInfos.put(Type.AUTOSYNC, new AdvancedAppSwitchBasicLock(isAutoSyncLocked,
                    appBase.getString(R.string.auto_sync_label),
                    appBase.getString(R.string.incoming_desc), Type.AUTOSYNC));
        }

        public List<BasicLockInfo> getBasicLocks() {
            return new ArrayList<>(basicLockInfos.values());
        }

        public BasicLockInfo getBasicLockInfo(Type type) {
            return basicLockInfos.get(type);
        }

        static class AdvancedAppSwitchBasicLock implements BasicLockInfo {

            private boolean isLocked;
            private String label;
            private String description;
            private Type type;

            AdvancedAppSwitchBasicLock(boolean isLocked, String label, String description, Type type) {
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

            public Type getType() {
                return type;
            }

            @Override
            public boolean equals(Object o) {
                if (o instanceof AdvancedAppSwitchBasicLock) {
                    return type == ((AdvancedAppSwitchBasicLock) o).getType();
                }
                return super.equals(o);
            }

            @Override
            public int hashCode() {
                return type.hashCode();
            }
        }

    }


}
