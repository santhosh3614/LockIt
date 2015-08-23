package com.smartminds.lockit.locklib.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.appsforbb.common.appbase.AppBase;
import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.LockAppListProvider;
import com.smartminds.lockit.locklib.UserProfile;
import com.smartminds.lockit.locklib.db.AdvancedAppLock;
import com.smartminds.lockit.locklib.db.AdvancedLockAppInfoTable;
import com.smartminds.lockit.locklib.db.Db;
import com.smartminds.lockit.locklib.db.LockItProfile;
import com.smartminds.lockit.locklib.others.LockLogger;

import java.util.ArrayList;
import java.util.List;

import static com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedLocks.Type;
import static com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedLocks.Type.INCOMMINGCALL;
import static com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedLocks.Type.RECENTASK;
import static com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedLocks.Type.TASKMANAGER;
import static com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedLocks.Type.UN_INSTALL;

/**
 * Created by santhosh on 4/7/15.
 */
public class BasicLockService {

    private static final String TAG = BasicLockService.class.getSimpleName();

    private static final int APPLOCK_SERVICE_REQUEST_CODE = 123;
    private static final long INTERVAL = 450;
    public static final String EXTRA_ADVANCEDAPPLOCK_STATUS = "advanced_applock_status";
    private Context mContext;
    public static final String ACTION_LOCKSERVICE_CHANGED = "com.appsforbb.applib.lockit.applocks_status_changed";
    public static final String ACTION_LOCK_PROFILE_CHANGED = "com.appsforbb.applib.lockit.lock_profile_changed";
    public static final String ACTION_APPS_INSTALL_UNINSTALL_CHANGE = "com.appsforbb.appslib.lockit.off_applocks.install_uninstall";
    public static final String ACTION_WATCH_RECENT_APP_CHANGE = "com.appsforbb.applib.lockit.watch_recent_apps";
    public static final String ACTION_LOCK_TELEPHONY_CALLS_CHANGE = "";
    public static final String ACTION_LOCK_TASK_MANGER_CHANGED = "";
    public static final String EXTRA_LOCK_PKG = "lock_pkg";
    public static final String EXTRA_LOCK_STATUS = "lock_pkg_status";
    public static final String ACTION_LOCK_UPDATED = "com.appsforbb.applib.lockit.lock_updated";
    //    public static final String ACTION_LOCK_DATA_CHANGED = "com.appsforbb.applib.lockit.lock_data_changed";
//    public static final String ACTION_LOCK_VIEW_CHANGED = "com.appsforbb.applib.lockit.lock_view_changed";
//    public static final String ACTION_CHANGE_LOCK_BG = "com.appsforbb.applib.lockit.change_lock_bg";
    private String currentApplication = "";
    private String previousApplication = "";
    private List<String> lockedPkg = new ArrayList<String>();
    private ComponentName topActivity;
    public boolean isWatchedRecentAppEnabled;
    private static Handler handler = new Handler();

    static final String RECENT_APPS_PACKAGENAME = "com.android.systemui";
    static final String RECENT_APPS_CLASS_NAME = "com.android.systemui.recent.RecentsActivity";
    private final String PACKAGE_INSTALLER_PKG = "com.android.packageinstaller";
    private LockScreenWindowManager lockScreenWindowManger;
    /**
     *
     */
    private Runnable lockRunnable = new Runnable() {
        @Override
        public void run() {
            checkRunningApplication();
            handler.postDelayed(this, INTERVAL);
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            AppLockLib lib = AppLockLib.getInstance();
            String action = intent.getAction();
            // used for enable and disabling app-locks
            if (action.equals(ACTION_LOCKSERVICE_CHANGED)) {
                if (lib.isEnabled()) {
                    handler.post(lockRunnable);
                    setRepeatingAlarm(AppBase.getAppContext());
                } else {
                    disableRepeatingAlarm(AppBase.getAppContext());
                    handler.removeCallbacks(lockRunnable);
                }
            }
            //disable applock service when screen off
            else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                handler.removeCallbacks(lockRunnable);
            }//enable applock service when screen on
            else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                if (lib.isEnabled()) {
                    handler.post(lockRunnable);
                }
            }
            //change lock profile
            else if (action.equals(ACTION_LOCK_PROFILE_CHANGED)) {
                initPackages();
            }
            //enable/disable install and uninstall service
            else if (action.equals(ACTION_APPS_INSTALL_UNINSTALL_CHANGE) ||
                    action.equals(ACTION_WATCH_RECENT_APP_CHANGE) ||
                    action.equals(ACTION_LOCK_TELEPHONY_CALLS_CHANGE) ||
                    action.equals(ACTION_LOCK_TASK_MANGER_CHANGED)) {
                boolean lockStatus = intent.getBooleanExtra(EXTRA_ADVANCEDAPPLOCK_STATUS, false);
                Type type = action.equals(ACTION_APPS_INSTALL_UNINSTALL_CHANGE) ? UN_INSTALL :
                        action.equals(ACTION_WATCH_RECENT_APP_CHANGE) ? RECENTASK :
                                action.equals(ACTION_LOCK_TELEPHONY_CALLS_CHANGE) ? INCOMMINGCALL : TASKMANAGER;
                switch (type) {
                    case RECENTASK:
                        isWatchedRecentAppEnabled = lockStatus;
                        break;
                    case TASKMANAGER:
                        //TODO taskmanger pending
                        break;
                    case UN_INSTALL:
                        if (lockStatus) {
                            lockedPkg.add(PACKAGE_INSTALLER_PKG);
                        } else {
                            lockedPkg.remove(PACKAGE_INSTALLER_PKG);
                        }
                        break;
                    case INCOMMINGCALL:
                        //TODO incomming call peding
                        break;
                }
            }
            //lock/unlock app here
            else if (intent.getAction().equals(ACTION_LOCK_UPDATED)) {
                String pkgName = intent.getStringExtra(EXTRA_LOCK_PKG);
                boolean lockStatus = intent.getBooleanExtra(EXTRA_LOCK_STATUS, false);
                if (!lockStatus) {
                    lockedPkg.remove(pkgName);
                } else if (lockStatus && !lockedPkg.contains(pkgName)) {
                    lockedPkg.add(pkgName);
                }
            }
//            else if (intent.getAction().equals(ACTION_LOCK_DATA_CHANGED)) {
//                lockScreenWindowManger.initLockScreen();
//            } else if (intent.getAction().equals(ACTION_LOCK_VIEW_CHANGED)) {
//                lockScreenWindowManger.initLockScreen();
//            } else if (intent.getAction().equals(ACTION_CHANGE_LOCK_BG)) {
//                lockScreenWindowManger.getLockView().initBackground();
//            }
        }
    };


    public BasicLockService(Context mContext) {
        this.mContext = mContext;
    }

    void initBasicLockService() {
        initPackages();
        lockScreenWindowManger = new LockScreenWindowManager(mContext);
        IntentFilter intentFilter = new IntentFilter(ACTION_LOCKSERVICE_CHANGED);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(ACTION_APPS_INSTALL_UNINSTALL_CHANGE);
        intentFilter.addAction(ACTION_WATCH_RECENT_APP_CHANGE);
        intentFilter.addAction(ACTION_LOCK_TELEPHONY_CALLS_CHANGE);
        intentFilter.addAction(ACTION_LOCK_TASK_MANGER_CHANGED);
        intentFilter.addAction(ACTION_LOCK_UPDATED);
        intentFilter.addAction(ACTION_LOCK_PROFILE_CHANGED);
//        intentFilter.addAction(ACTION_LOCK_DATA_CHANGED);
//        intentFilter.addAction(ACTION_LOCK_VIEW_CHANGED);
//        intentFilter.addAction(ACTION_CHANGE_LOCK_BG);
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void initPackages() {
        lockedPkg.clear();
        AppLockLib lib = AppLockLib.getInstance();
        UserProfile profile = lib.getUserProfileProvider().getProfile();
        AdvancedLockAppInfoTable advancedLockAppInfoTable = Db.getInstance().getAdvancedLockAppInfoTable();
        AdvancedAppLock advancedAppLock = advancedLockAppInfoTable.getAdvancedAppLock(((LockItProfile) profile).getId());
        LockAppListProvider appListProvider = lib.getAppListProvider();
        if (lib.isEnabled()) {
            broadcastReceiver.onReceive(mContext, new Intent(ACTION_LOCKSERVICE_CHANGED));
        }
        if (advancedAppLock.getAdvancedLock().getBasicLockInfo(UN_INSTALL).isLocked()) {
            lockedPkg.add(PACKAGE_INSTALLER_PKG);
        }
        isWatchedRecentAppEnabled = advancedAppLock.getAdvancedLock().getBasicLockInfo(RECENTASK).isLocked();
        lockedPkg.addAll(appListProvider.getLockedPackages(profile));
    }

    private void checkRunningApplication() {
        String packageName = getCurrentPkgName();
        LockLogger.d(TAG, "Pkg....:" + packageName + " CApp:" + currentApplication + " PApp:" + previousApplication);
        if (isValidPackageName(packageName)) {
            if (lockWatchRecentApps(packageName)) {
                return;
            }
            if (lockedPkg.contains(packageName)) {
//                lockScreenWindowManger.hideScreen(false, packageName);
                Toast.makeText(mContext, "Pkg:" + packageName + " is Locked.", Toast.LENGTH_LONG).show();
                return;
            } else {
//                if (!lockScreenWindowManger.isLockScreenHidden()) {
//                    lockScreenWindowManger.hideScreen(true, null);
//                }
            }
        }
    }

    private boolean lockWatchRecentApps(String packageName) {
        if (packageName.equals(RECENT_APPS_PACKAGENAME)) {
            if (isWatchedRecentAppEnabled) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    if (topActivity.getClassName().equals(RECENT_APPS_CLASS_NAME)) {
                        LockLogger.d(TAG, "pushLockScreen....called");
//                        lockScreenWindowManger.hideScreen(false, packageName);
                        Toast.makeText(mContext, "Pkg:" + packageName + " is Locked.", Toast.LENGTH_LONG).show();
                    }
                }
            }
            return true;
        }
        return false;
    }

    private String getCurrentPkgName() {
        String topActivityPackage = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> runningTasks =
                    ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
            ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
            this.topActivity = runningTaskInfo.topActivity;
            topActivityPackage = runningTaskInfo.topActivity.getPackageName();
        } else {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                    ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
            if (runningAppProcesses != null) {
                ActivityManager.RunningAppProcessInfo runningAppProcessInfo = runningAppProcesses.get(0);
                topActivityPackage = runningAppProcessInfo.pkgList[0];
            }
        }
        return topActivityPackage;
    }

    private boolean isValidPackageName(String packageName) {
        if (previousApplication.equals(packageName)) {
            return false;
        }
        previousApplication = packageName;
        if (!currentApplication.equals(packageName)) {
            currentApplication = packageName;
            return true;
        }
        return false;
    }

    static void setRepeatingAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AppLockService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, APPLOCK_SERVICE_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30 * 1000, pendingIntent);
    }

    static void disableRepeatingAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AppLockService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, APPLOCK_SERVICE_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    void uninitBasicLockService() {
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).unregisterReceiver(broadcastReceiver);
        handler.removeCallbacks(lockRunnable);
    }
}
