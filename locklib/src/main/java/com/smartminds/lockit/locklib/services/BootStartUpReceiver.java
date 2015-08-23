package com.smartminds.lockit.locklib.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.appsforbb.common.appbase.AppBase;
import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.others.LockLogger;

import static com.smartminds.lockit.locklib.services.BasicLockService.setRepeatingAlarm;

public class BootStartUpReceiver extends BroadcastReceiver {

    private static final String TAG = BootStartUpReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        LockLogger.d(TAG, "onReceive()...called");
        intent = new Intent(AppBase.getAppContext(), AppLockService.class);
        context.startService(intent);
        if (AppLockLib.getInstance().isEnabled()) {
            setRepeatingAlarm(context);
        }
    }
}
