package com.smartminds.lockit.locklib;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.appsforbb.common.appbase.AppBase;
import com.smartminds.lockit.locklib.common.lock.LockScreen;
import com.smartminds.lockit.locklib.common.lockscreen.background.BackgroundThemeProviderImpl;
import com.smartminds.lockit.locklib.db.LockAppListProviderImpl;
import com.smartminds.lockit.locklib.db.UserProfileDbProvider;
import com.smartminds.lockit.locklib.services.AppLockService;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class AppLockLib {
    public static final String ACTION_LOGIN_WIFI_LOCK = "com.appsforbb.applib.applock.wifi_lock";
    public static final String ACTION_LOGIN_BLUETOOTH_LOCK = "com.appsforbb.applib.applock.bluetooth_lock";
    public static final String ACTION_LOGIN_MOBILE_DATA_LOCK = "com.appsforbb.applib.applock.mobile_data_lock";
    public static final String ACTION_LOGIN_MOBILE_SYNC_LOCK = "com.appsforbb.applib.applock.mobile_sync_lock";

    private static AppLockLib instance;
    private LockItSettings settings = new LockItSettings(AppLockLib.class.getName());

    private AppLockLib() {
    }

    public static AppLockLib getInstance() {
        if (instance == null) {
            instance = new AppLockLib();
//            AppBase.getAppContext().startService(new Intent(AppBase.getAppContext(), AppLockService.class));
        }
        return instance;
    }

    public void init(Class<? extends Activity> loginActivity) {
        instance.settings.setString(LockItSettings.LOGIN_ACTIVITY, loginActivity.getCanonicalName());
    }

    public Object getCurrentLockScreen() {
        try {
            String className = settings.getString("lockview", null);
            if (className != null) {
                try {
                    return Class.forName(className).getConstructor(Context.class).newInstance(AppBase.getAppContext());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    public <T extends View & LockScreen> void setCurrentLockScreen(T lockscreen) {
        settings.setString("lockview", lockscreen.getClass().getName());
    }

    public LockAppListProvider getAppListProvider() {
        return new LockAppListProviderImpl();
    }

    public UserProfileProvider getUserProfileProvider() {
        return new UserProfileDbProvider();
    }


    public BackgroundThemeProvider getBackgroundThemeProvider() {
        return new BackgroundThemeProviderImpl();
    }


    public boolean isEnabled() {
        return settings.getBoolean(LockItSettings.IS_LOCKIT_ENABLED, true);
    }

    public void setEnabled(boolean enabled) {
        settings.setBoolean(LockItSettings.IS_LOCKIT_ENABLED, enabled);
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(new Intent(AppLockService.ACTION_APPLOCKS_CHANGE));
    }

    public boolean isLockInstallUninstall() {
        return settings.getBoolean(LockItSettings.IS_LOCK_UNINSTALL_UNINSTALL, true);
    }

    public void setLockInstallUninstall(boolean enabled) {
        settings.setBoolean(LockItSettings.IS_LOCK_UNINSTALL_UNINSTALL, enabled);
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(new Intent(AppLockService.ACTION_LOCK_APP_INSTALL_AND_UNINSTALLATION_CHANGE));
    }

    public boolean isLockSettings() {
        return settings.getBoolean(LockItSettings.IS_LOCK_SETTING_APP, true);
    }

    public void setLockSettings(boolean enabled) {
        settings.setBoolean(LockItSettings.IS_LOCK_SETTING_APP, enabled);
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(new Intent(AppLockService.ACTION_LOCK_SETTINGS_APP_CHANGE));
    }

    public boolean isLockRecent() {
        return settings.getBoolean(LockItSettings.IS_LOCK_RECENT_APP, true);
    }

    public void setLockRecent(boolean enabled) {
        settings.setBoolean(LockItSettings.IS_LOCK_RECENT_APP, enabled);
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(new Intent(AppLockService.ACTION_WATCH_RECENT_APP_CHANGE));
    }

    public String getRecoveryEmail() {
        return settings.getString(LockItSettings.RECOVERY_EMAIL, null);
    }

    public void setRecoveryEmail(String email) {
        settings.setString(LockItSettings.RECOVERY_EMAIL, email);
    }

    public String[] getDeviceGmailIds(boolean excludeRecoveryEmail) {
        Account[] accounts = AccountManager.get(AppBase.getAppContext()).getAccountsByType("com.google");
        List<String> gmails = new ArrayList<>(accounts.length);
        String recoveryGmail = getRecoveryEmail();
        for (int i = 0; i < accounts.length; i++) {
            if (excludeRecoveryEmail && recoveryGmail != null && accounts[i].name.equals(recoveryGmail)) {
                continue;
            }
            gmails.add(accounts[i].name);
        }
        return gmails.toArray(new String[gmails.size()]);
    }

    public boolean isWifiLockEnabled() {
        return settings.getBoolean(LockItSettings.IS_LOCK_WIFI, false);
    }

    public void setWifiLockEnabled(boolean enabled) {
        settings.setBoolean(LockItSettings.IS_LOCK_WIFI, enabled);
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(new Intent(AppLockService.ACTION_LOCK_WIFI_CHNAGE));
    }

    public boolean isBluetoothEnabled() {
        return settings.getBoolean(LockItSettings.IS_LOCK_BLUETOOTH, false);
    }

    public void setBluetoothLockEnabled(boolean enabled) {
        settings.setBoolean(LockItSettings.IS_LOCK_BLUETOOTH, enabled);
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(new Intent(AppLockService.ACTION_LOCK_BLUETOOTH_CHANGE));
    }

    public boolean isMobileDataLockEnabled() {
        return settings.getBoolean(LockItSettings.IS_LOCK_MOBILE_DATA, false);
    }

    public void setMobileDataLockEnabled(boolean enabled) {
        settings.setBoolean(LockItSettings.IS_LOCK_MOBILE_DATA, enabled);
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(new Intent(AppLockService.ACTION_LOCK_MOBILE_DATA_CHANGE));
    }

    public boolean isAutoSyncLockEnabled() {
        return settings.getBoolean(LockItSettings.IS_AUTO_SYNC_LOCKED, false);
    }

    public void setAutoSyncLockEnabled(boolean enabled) {
        settings.setBoolean(LockItSettings.IS_AUTO_SYNC_LOCKED, enabled);
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(new Intent(AppLockService.ACTION_LOCK_AUTO_SYNC_CHANGE));
    }

    public Class<?> getLoginActivity() throws ClassNotFoundException {
        return Class.forName(settings.getString(LockItSettings.LOGIN_ACTIVITY, null));
    }

}