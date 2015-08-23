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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static com.smartminds.lockit.locklib.services.BasicLockService.ACTION_LOCKSERVICE_CHANGED;

public class AppLockLib {

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
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(
                new Intent(ACTION_LOCKSERVICE_CHANGED));
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

    public Class<?> getLoginActivity() throws ClassNotFoundException {
        return Class.forName(settings.getString(LockItSettings.LOGIN_ACTIVITY, null));
    }

}