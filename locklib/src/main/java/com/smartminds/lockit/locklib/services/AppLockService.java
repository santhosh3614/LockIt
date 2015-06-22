package com.smartminds.lockit.locklib.services;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.appsforbb.common.appbase.AppBase;
import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.LockAppListProvider;
import com.smartminds.lockit.locklib.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class AppLockService extends Service {

    private static final int NOTIFICATION_ID = 123;
    private static final int APPLOCK_SERVICE_REQUEST_CODE = 123;
    private static final long INTERVAL = 450;
    private static final String TAG = AppLockService.class.getSimpleName();
    public static final String EXTRA_LOCK_PKG = "lock_pkg";
    public static final String EXTRA_LOCK_STATUS = "lock_pkg_status";
    public static final String ACTION_LOCK_WIFI_CHNAGE = "com.appsforbb.applib.lockit.wifi_lock_status_changed";
    public static final String ACTION_LOCK_BLUETOOTH_CHANGE = "com.appsforbb.applib.lockit.bluetooth_lock_status_changed";
    public static final String ACTION_LOCK_MOBILE_DATA_CHANGE = "com.appsforbb.applib.lockit.mobile_data_lock_status_changed";
    public static final String ACTION_LOCK_AUTO_SYNC_CHANGE = "com.appsforbb.applib.lockit.auto_sync_lock_status_changed";
    public static final String ACTION_LOCK_APP_INSTALL_AND_UNINSTALLATION_CHANGE = "com.appsforbb.appslib.lockit.off_applocks.install_uninstall";
    public static final String ACTION_LOCK_UPDATED = "com.appsforbb.applib.lockit.lock_updated";
    public static final String ACTION_APPLOCKS_CHANGE = "com.appsforbb.applib.lockit.applocks_status_changed";
    public static final String ACTION_WATCH_RECENT_APP_CHANGE = "com.appsforbb.applib.lockit.watch_recent_apps";
    public static final String ACTION_LOCK_SETTINGS_APP_CHANGE = "com.appsforbb.applib.lockit.lock_settings";
    private static final String SETTINGS_APP_PACKAGENAME = "com.android.settings";
    public static final String ACTION_LOCK_DATA_CHANGED = "com.appsforbb.applib.lockit.lock_data_changed";
    public static final String ACTION_LOCK_VIEW_CHANGED = "com.appsforbb.applib.lockit.lock_view_changed";
    public static final String ACTION_LOCK_PROFILE_CHANGED = "com.appsforbb.applib.lockit.lock_profile_changed";
    public static final String ACTION_CHANGE_LOCK_BG = "com.appsforbb.applib.lockit.change_lock_bg";
    private String currentApplication = "";
    private String previousApplication = "";
    private List<String> lockedPkg = new ArrayList<String>();
    private ComponentName topActivity;
    public boolean isWatchedRecentAppEnabled;
    private LockAppListProvider appListProvider;
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
    /**
     * used for enable and disabling app-locks
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            AppLockLib lib = AppLockLib.getInstance();
            if (intent.getAction().equals(ACTION_APPLOCKS_CHANGE)) {
                if (lib.isEnabled()) {
                    handler.post(lockRunnable);
                    setRepeatingAlarm(AppBase.getAppContext());
                } else {
                    disableRepeatingAlarm(AppBase.getAppContext());
                    handler.removeCallbacks(lockRunnable);
                }
            } else if (intent.getAction().equals(ACTION_LOCK_APP_INSTALL_AND_UNINSTALLATION_CHANGE)) {
                if (lib.isLockInstallUninstall()) {
                    lockedPkg.add(PACKAGE_INSTALLER_PKG);
                } else {
                    lockedPkg.remove(PACKAGE_INSTALLER_PKG);
                }
            } else if (intent.getAction().equals(ACTION_LOCK_PROFILE_CHANGED)) {
                lockedPkg.clear();
                initPackages();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                handler.removeCallbacks(lockRunnable);
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if (lib.isEnabled()) {
                    handler.post(lockRunnable);
                }
            } else if (intent.getAction().equals(ACTION_LOCK_UPDATED)) {
                String pkgName = intent.getStringExtra(EXTRA_LOCK_PKG);
                boolean lockStatus = intent.getBooleanExtra(EXTRA_LOCK_STATUS, false);
                if (!lockStatus) {
                    lockedPkg.remove(pkgName);
                } else if (lockStatus && !lockedPkg.contains(pkgName)) {
                    lockedPkg.add(pkgName);
                }
            } else if (intent.getAction().equals(ACTION_LOCK_SETTINGS_APP_CHANGE)) {
                if (lib.isLockSettings() && !lockedPkg.contains(SETTINGS_APP_PACKAGENAME)) {
                    lockedPkg.add(SETTINGS_APP_PACKAGENAME);
                } else {
                    lockedPkg.remove(SETTINGS_APP_PACKAGENAME);
                }
            } else if (intent.getAction().equals(ACTION_WATCH_RECENT_APP_CHANGE)) {
                isWatchedRecentAppEnabled = lib.isLockRecent();
            } else if (intent.getAction().equals(ACTION_LOCK_DATA_CHANGED)) {
                lockScreenWindowManger.initLockScreen();
            }else if(intent.getAction().equals(ACTION_LOCK_VIEW_CHANGED)){
                lockScreenWindowManger.initLockScreen();
            }else if(intent.getAction().equals(ACTION_CHANGE_LOCK_BG)){
                lockScreenWindowManger.getLockView().initBackground();
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        initPackages();
        lockScreenWindowManger = new LockScreenWindowManager(this);
        IntentFilter intentFilter = new IntentFilter(ACTION_APPLOCKS_CHANGE);
        intentFilter.addAction(ACTION_LOCK_APP_INSTALL_AND_UNINSTALLATION_CHANGE);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(ACTION_LOCK_UPDATED);
        intentFilter.addAction(ACTION_WATCH_RECENT_APP_CHANGE);
        intentFilter.addAction(ACTION_LOCK_SETTINGS_APP_CHANGE);
        intentFilter.addAction(ACTION_LOCK_PROFILE_CHANGED);
        intentFilter.addAction(ACTION_LOCK_DATA_CHANGED);
        intentFilter.addAction(ACTION_LOCK_VIEW_CHANGED);
        intentFilter.addAction(ACTION_CHANGE_LOCK_BG);
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).registerReceiver(broadcastReceiver, intentFilter);
        addForegroundNotification();
    }

    private void initPackages() {
        AppLockLib lib = AppLockLib.getInstance();
        appListProvider = lib.getAppListProvider();
        if (lib.isEnabled()) {
            broadcastReceiver.onReceive(this, new Intent(ACTION_APPLOCKS_CHANGE));
        }
        if (lib.isLockInstallUninstall()) {
            lockedPkg.add(PACKAGE_INSTALLER_PKG);
        }
        isWatchedRecentAppEnabled = lib.isLockRecent();
        UserProfile userProfile = lib.getUserProfileProvider().getProfile();
        lockedPkg.addAll(appListProvider.getLockedPackages(userProfile));
        if (lib.isLockSettings()) {
            lockedPkg.add(SETTINGS_APP_PACKAGENAME);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onstart command called...."+this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy.....called");
        if (!(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            stopForeground(true);
        }
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).unregisterReceiver(broadcastReceiver);
        handler.removeCallbacks(lockRunnable);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void addForegroundNotification() {
        if (!(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)) {
            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setTicker("LockIt is running");
            Intent i = new Intent();
            PendingIntent pi = PendingIntent.getActivity(this, 0, i,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setPriority(Notification.PRIORITY_MIN);
            builder.addAction(android.R.drawable.ic_dialog_alert, "LockIt", pi);
            startForeground(NOTIFICATION_ID, builder.build());
        }
    }

    private void checkRunningApplication() {
        String packageName = getCurrentPkgName();
        Log.d(TAG, "Pkg....:" + packageName + " CApp:" + currentApplication + " PApp:" + previousApplication);
        if (isValidPackageName(packageName)) {
            if (lockWatchRecentApps(packageName)) {
                return;
            }
            if (lockedPkg.contains(packageName)) {
                lockScreenWindowManger.hideScreen(false, packageName);
                Toast.makeText(this, "Pkg:" + packageName + " is Locked.", Toast.LENGTH_LONG).show();
                return;
            } else {
                if (!lockScreenWindowManger.isLockScreenHidden()) {
                    lockScreenWindowManger.hideScreen(true, null);
                }
            }
        }
    }

    private boolean lockWatchRecentApps(String packageName) {
        if (packageName.equals(RECENT_APPS_PACKAGENAME)) {
            if (isWatchedRecentAppEnabled) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    if (topActivity.getClassName().equals(RECENT_APPS_CLASS_NAME)) {
                        Log.d(TAG, "pushLockScreen....called");
                        lockScreenWindowManger.hideScreen(false, packageName);
                        Toast.makeText(this, "Pkg:" + packageName + " is Locked.", Toast.LENGTH_LONG).show();
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
                    ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getRunningTasks(1);
            ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
            this.topActivity = runningTaskInfo.topActivity;
            topActivityPackage = runningTaskInfo.topActivity.getPackageName();
        } else {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                    ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getRunningAppProcesses();
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

}
