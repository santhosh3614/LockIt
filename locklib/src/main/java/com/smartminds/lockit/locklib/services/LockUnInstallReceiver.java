package com.smartminds.lockit.locklib.services;


import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by santhoshkumar on 16/4/15.
 */
public class LockUnInstallReceiver extends DeviceAdminReceiver {
    private static final String TAG=LockUnInstallReceiver.class.getSimpleName();

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Log.d(TAG, "onDisabled.......");
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Log.d(TAG,"onEnabled.......");
    }

    @Override
    public void onLockTaskModeEntering(Context context, Intent intent, String pkg) {
        super.onLockTaskModeEntering(context, intent, pkg);
        Log.d(TAG,"onLockTaskModeEntering.......");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        Log.d(TAG,"onDisableRequested.......");
        return super.onDisableRequested(context, intent);
    }

    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
        super.onLockTaskModeExiting(context, intent);
        Log.d(TAG,"onLockTaskModeExiting.......");
    }
    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
        Log.d(TAG,"onPasswordChanged.......");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG,"onReceive......."+intent.getAction());
    }
}
