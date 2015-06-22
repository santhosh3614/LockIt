package com.smartminds.lockit.locklib.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appsforbb.common.appbase.AppBase;
import com.smartminds.lockit.locklib.AppLockLib;

public class BootStartUpReceiver extends BroadcastReceiver{

    private static final String TAG=BootStartUpReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(BootStartUpReceiver.class.getSimpleName(), "onReceive()...called");
        intent = new Intent(AppBase.getAppContext(), AppLockService.class);
        context.startService(intent);
        if (AppLockLib.getInstance().isEnabled()) {
            AppLockService.setRepeatingAlarm(context);
        }
    }
}
