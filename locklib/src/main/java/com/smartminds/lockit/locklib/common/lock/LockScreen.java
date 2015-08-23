package com.smartminds.lockit.locklib.common.lock;

import com.appsforbb.common.annotations.NonNull;

public interface LockScreen {
    public enum LockType {
        NUMBER_LOCK, PATTERN_LOCK, PASSWORD_LOCK, GESTURE_LOCK
    }

    public interface CreatePassCallBack {
        public void onCreatePassCancel();

        public void onCreatePassValid(LockData data);

        public void onCreatePassInvalid(LockData data);
    }

    public interface ReconfirmPassCallBack {
        public void onConfirmPassCancel();

        public void onConfirmPassMismatch(LockData oldData, LockData newData);

        public void onConfirmPassMatched(LockData data);
    }

    public interface VerifyPassCallBack {
        public void onVerifyPassCancel();

        public void onVerifyPassMismatch(LockData oldData, LockData newData);

        public void onVerifyPassMatched(LockData data);
    }

    public int MODE_CREATE_PASS = 0;

    public int MODE_RECONFIRM_PASS = 1;

    public int MODE_VERIFY_PASS = 2;

    public LockType getType();

    public void setModeCreatePass(@NonNull CreatePassCallBack callBack);

    public void setModeReconfirmPass(@NonNull LockData data, @NonNull ReconfirmPassCallBack callBack) throws IllegalArgumentException;

    public void setModeVerifyPass(@NonNull LockData data, @NonNull VerifyPassCallBack callback) throws IllegalArgumentException;
}
