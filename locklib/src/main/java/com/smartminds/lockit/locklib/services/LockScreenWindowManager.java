package com.smartminds.lockit.locklib.services;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.appsforbb.common.appbase.AppBase;
import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.common.lock.LockData;
import com.smartminds.lockit.locklib.common.lock.LockView;
import com.smartminds.lockit.locklib.common.lockscreen.LockScreenProvider;

public class LockScreenWindowManager implements LockView.VerifyPassCallBack {
    private final WindowManager mWindowManger;
    private final WindowManager.LayoutParams mWindowLayoutparms;
    private final LockScreenProvider lockScreenProvider;
    private boolean isAttached;
    private LockView lockView;

    public LockScreenWindowManager(Context context) {
        this.mWindowManger = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.mWindowLayoutparms = getWindowLayoutparams();
        lockScreenProvider = new LockScreenProvider();
        initLockScreen();
    }

    void initLockScreen() {
        Object current = AppLockLib.getInstance().getCurrentLockScreen();
        if (current != null) {
            lockView = (LockView) current;
            lockView.setModeVerifyPass(lockScreenProvider.getLockScreenData(lockView.getType()), this);
        }
    }

    private WindowManager.LayoutParams getWindowLayoutparams() {
        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_PHONE, 1064, 1);
        mWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        return mWindowParams;
    }

    LockView getLockView() {
        return lockView;
    }

    public void hideScreen(boolean hide, String packageName) {
        if(lockView!=null){
            if (hide) {
                if (isAttached) {
                    mWindowManger.removeView((View) lockView);
                    lockView.resetInput();
                    isAttached = false;
                }
            } else {
                if (!isAttached) {
                    mWindowManger.addView((View) lockView, mWindowLayoutparms);
                    isAttached = true;
                }
            }
        }
    }

    public boolean isLockScreenHidden() {
        return !isAttached;
    }

    @Override
    public void onVerifyPassCancel() {
        hideScreen(true, null);
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppBase.getAppContext().startActivity(startMain);
    }

    @Override
    public void onVerifyPassMismatch(LockData oldData, LockData newData) {

    }

    @Override
    public void onVerifyPassMatched(LockData data) {
        lockView.resetInput();
        hideScreen(true, null);
    }
}
