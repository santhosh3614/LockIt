package com.smartminds.lockit.locklib.common.lock;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;

import com.appsforbb.common.annotations.NonNull;
import com.appsforbb.common.annotations.Nullable;
import com.smartminds.lockit.locklib.common.widget.CustomView;

public abstract class LockViewBase extends CustomView implements LockView {

    private int mode;
    private LockData lockData;
    private CreatePassCallBack createPassCallBack;
    private ReconfirmPassCallBack reconfirmPassCallBack;
    private VerifyPassCallBack verifyPassCallBack;

    public LockViewBase(Context context) {
        super(context);
    }

    public LockViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public LockViewBase(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void cancelInput() {
        switch (mode) {
            case MODE_CREATE_PASS:
                createPassCallBack.onCreatePassCancel();
                return;
            case MODE_RECONFIRM_PASS:
                reconfirmPassCallBack.onConfirmPassCancel();
                return;
            case MODE_VERIFY_PASS:
                verifyPassCallBack.onVerifyPassCancel();
                return;
        }
        resetInput();
        invalidate();
    }

    @Override
    public void validateInput() {
        LockData inputData = getInputData();
        switch (mode) {
            case MODE_CREATE_PASS:
                validateCreatePassInput(inputData);
                return;
            case MODE_RECONFIRM_PASS:
                validateReconfirmPassInput(inputData);
                return;
            case MODE_VERIFY_PASS:
                validateVerifyPassInput(inputData);
                return;
        }
        resetInput();
        invalidate();
    }

    private void validateCreatePassInput(LockData inputData) {
        if (isLockDataCompatible(inputData)) {
            createPassCallBack.onCreatePassValid(inputData);
        } else {
            createPassCallBack.onCreatePassInvalid(inputData);
        }
    }

    private void validateReconfirmPassInput(LockData inputData) {
        if (lockData.matches(inputData)) {
            reconfirmPassCallBack.onConfirmPassMatched(inputData);
        } else {
            reconfirmPassCallBack.onConfirmPassMismatch(lockData, inputData);
        }
    }

    private void validateVerifyPassInput(LockData inputData) {
        if(lockData.matches(inputData)) {
            verifyPassCallBack.onVerifyPassMatched(inputData);
        } else {
            verifyPassCallBack.onVerifyPassMismatch(lockData, inputData);
        }
    }

    @Override
    public final void setModeCreatePass(@NonNull CreatePassCallBack callback) {
        setMode(null, MODE_CREATE_PASS, callback, null, null);
    }

    @Override
    public final void setModeReconfirmPass(@NonNull LockData data, @NonNull ReconfirmPassCallBack callback) throws IllegalArgumentException {
        if (!isLockDataCompatible(data))
            throw new IllegalArgumentException("Invalid lock data");

        setMode(data, MODE_RECONFIRM_PASS, null, callback, null);
    }

    @Override
    public final void setModeVerifyPass(@NonNull LockData data, @NonNull VerifyPassCallBack callback) throws IllegalArgumentException {
        if (!isLockDataCompatible(data))
            throw new IllegalArgumentException("Invalid lock data");
        setMode(data, MODE_VERIFY_PASS, null, null, callback);
    }

    private void setMode(@Nullable LockData data, int mode, @Nullable CreatePassCallBack createPassCallBack,
                         @Nullable ReconfirmPassCallBack reconfirmPassCallBack, @Nullable VerifyPassCallBack verifyPassCallBack) {
        resetInput();
        this.lockData = data;
        this.mode = mode;
        this.createPassCallBack = createPassCallBack;
        this.reconfirmPassCallBack = reconfirmPassCallBack;
        this.verifyPassCallBack = verifyPassCallBack;
    }

}
