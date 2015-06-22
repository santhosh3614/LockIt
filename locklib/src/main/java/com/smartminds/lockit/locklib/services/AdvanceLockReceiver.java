package com.smartminds.lockit.locklib.services;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.smartminds.lockit.locklib.AppLockLib;

public class AdvanceLockReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        AppLockLib appLockLib = AppLockLib.getInstance();
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager conMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conMngr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (networkInfo.isConnected() && appLockLib.isMobileDataLockEnabled()) {
                    startLoginActivity(context, AppLockLib.ACTION_LOGIN_MOBILE_DATA_LOCK);
                }
            }
        } else if (intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled() && appLockLib.isWifiLockEnabled()) {
                startLoginActivity(context, AppLockLib.ACTION_LOGIN_WIFI_LOCK);
            }
        } else if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            if (state == BluetoothAdapter.STATE_ON && appLockLib.isBluetoothEnabled()) {
                startLoginActivity(context, AppLockLib.ACTION_LOGIN_BLUETOOTH_LOCK);
            }
        } else if (intent.getAction().equals("com.android.sync.SYNC_CONN_STATUS_CHANGED")) {
            if (appLockLib.isAutoSyncLockEnabled() && ContentResolver.getMasterSyncAutomatically()) {
                startLoginActivity(context, AppLockLib.ACTION_LOGIN_MOBILE_SYNC_LOCK);
            }
        }
    }

    private void startLoginActivity(Context context, String action) {
        try {
            AppLockLib appLockLib = AppLockLib.getInstance();
            Intent intent = new Intent(context, appLockLib.getLoginActivity());
            intent.setAction(action);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ClassNotFoundException e) {
        }
    }

}