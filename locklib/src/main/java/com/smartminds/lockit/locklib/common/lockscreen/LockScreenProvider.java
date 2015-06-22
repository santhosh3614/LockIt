package com.smartminds.lockit.locklib.common.lockscreen;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.appsforbb.common.appbase.AppBase;
import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.LockItSettings;
import com.smartminds.lockit.locklib.common.lock.LockData;
import com.smartminds.lockit.locklib.common.lock.LockScreen;
import com.smartminds.lockit.locklib.common.lock.WordLockData;
import com.smartminds.lockit.locklib.services.AppLockService;

public class LockScreenProvider {

    private LockItSettings settings = new LockItSettings(AppLockLib.class.getName());

    public LockScreen.LockType getLockScreenType() {
        int lock_type = settings.getInt("lock_type", -1);
        if (lock_type == -1) {
            return null;
        }
        return LockScreen.LockType.values()[lock_type];
    }

    public void setLockScreenType(LockScreen.LockType lockType) {
        settings.setInt("lock_type", lockType.ordinal());
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(new Intent(AppLockService.ACTION_LOCK_VIEW_CHANGED));
    }

    public void setLockData(LockScreen.LockType lockType, LockData lockData) {
        if (lockType == LockScreen.LockType.NUMBER_LOCK) {
            WordLockData wordLockData = (WordLockData) lockData;
            settings.setString(LockItSettings.NUM_LOCK_KEY, wordLockData.getCode());
            settings.setString(LockItSettings.NUM_LOCK_HINT, wordLockData.getHint());
        }
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(new Intent(AppLockService.ACTION_LOCK_DATA_CHANGED));
    }

    public LockData getLockScreenData(LockScreen.LockType lockType) {
        if (lockType == LockScreen.LockType.NUMBER_LOCK) {
            return new WordLockData(settings.getString(LockItSettings.NUM_LOCK_KEY, null),
                    settings.getString(LockItSettings.NUM_LOCK_HINT, null));
        }
        return null;
    }

}
