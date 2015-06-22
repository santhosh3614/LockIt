package com.smartminds.lockit.locklib.common.lock;

public interface LockView extends LockScreen {

    public void resetInput();

    public void cancelInput();

    public void validateInput();

    public LockData getInputData();

    public boolean isLockDataCompatible(LockData data);

    public void initBackground();
}