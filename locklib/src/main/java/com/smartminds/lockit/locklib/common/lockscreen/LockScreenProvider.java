package com.smartminds.lockit.locklib.common.lockscreen;

import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.LockItSettings;
import com.smartminds.lockit.locklib.common.lock.LockData;
import com.smartminds.lockit.locklib.common.lock.LockScreen;
import com.smartminds.lockit.locklib.common.lock.LockScreen.LockType;
import com.smartminds.lockit.locklib.common.lock.WordLockData;

public class LockScreenProvider {

    private LockItSettings settings = new LockItSettings(AppLockLib.class.getName());

    public LockType getLockScreenType() {
        int lock_type = settings.getInt("lock_type", -1);
        if (lock_type == -1) {
            return null;
        }
        return LockType.values()[lock_type];
    }

    public void setLockScreenType(LockType lockType) {
        settings.setInt("lock_type", lockType.ordinal());
//        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(
//                new Intent(BasicLockService.ACTION_LOCK_VIEW_CHANGED));
    }

    public void setLockData(LockType lockType, LockData lockData) {
        if (lockType == LockType.NUMBER_LOCK) {
            WordLockData wordLockData = (WordLockData) lockData;
            settings.setString(LockItSettings.NUM_LOCK_KEY, wordLockData.getCode());
            settings.setString(LockItSettings.NUM_LOCK_HINT, wordLockData.getHint());
        }
//        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(new Intent(AppLockService.ACTION_LOCK_DATA_CHANGED));
    }

    public LockData getLockScreenData(LockType lockType) {
        if (lockType == LockType.NUMBER_LOCK) {
            return new WordLockData(settings.getString(LockItSettings.NUM_LOCK_KEY, null),
                    settings.getString(LockItSettings.NUM_LOCK_HINT, null));
        }
        return null;
    }

}
