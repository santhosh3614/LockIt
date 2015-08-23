package com.smartminds.lockit.locklib.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.smartminds.lockit.locklib.others.LockLogger;

public class AppLockService extends Service {

    private static final String TAG = AppLockService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 123;
    private BasicLockService appLockService;

    @Override
    public void onCreate() {
        super.onCreate();
        LockLogger.d(TAG,"onCreate called....");
        appLockService = new BasicLockService(this);
        appLockService.initBasicLockService();
        addForegroundNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        LockLogger.d(TAG,"onBind called....");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LockLogger.d(TAG, "onstart command called...." + this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LockLogger.d(TAG, "onDestroy.....called");
        if (!(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            stopForeground(true);
        }
        appLockService.uninitBasicLockService();
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

}
