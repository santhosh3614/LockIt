package com.smartminds.lockit.locklib.common.lock;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.Log;

import com.appsforbb.common.annotations.NonNull;

public class NumLockView extends LockViewBase {
    public interface OnDrawNumLockView {
        void onDrawNumLockView(Canvas canvas, String input);
    }

    private static final String TAG = "NumLock";
    private int numDigits = 4;
    private String input = "";
    private OnDrawNumLockView delegate;

    public NumLockView(Context context) {
        super(context);
    }

    public NumLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public NumLockView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public LockType getType() {
        return LockType.NUMBER_LOCK;
    }

    public void setNumDigits(int numDigits) {
        this.numDigits = numDigits;
    }

    public int getNumDigits() {
        return numDigits;
    }

    public void setDrawingDelegate(OnDrawNumLockView delegate) {
        this.delegate = delegate;
    }

    @Override
    protected final void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (delegate != null) {
            delegate.onDrawNumLockView(canvas, input);
        }
    }

    @Override
    public void resetInput() {
        this.input = "";
    }

    public String getInput() {
        return input;
    }

    @Override
    public LockData getInputData() {
        return new WordLockData(input);
    }

    public boolean addDigit(int digit) {
        if (digit < 0 || digit > 9)
            return false;

        if (input.length() >= numDigits)
            return false;

        input = input + digit;

        if (input.length() == numDigits) {
            post(new Runnable() {
                @Override
                public void run() {
                    validateInput();
                }
            });
        }
        invalidate();
        return true;
    }

    public boolean deleteDigit() {
        if (input.length() == 0) {
            invalidate();
            return false;
        }
        input = input.substring(0, input.length() - 1);
        invalidate();
        return true;
    }

    @Override
    public boolean isLockDataCompatible(@NonNull LockData data) {
        if (!(data instanceof WordLockData)) {
            Log.e(TAG, "Invalid type of lock data for " + getType());
            return false;
        }
        WordLockData wordlockdata = (WordLockData) data;
        if (wordlockdata.code.length() != numDigits) {
            Log.e(TAG, "Invalid pass code length for " + getType());
            return false;
        }
        char[] digits = wordlockdata.code.toCharArray();
        for (char digit : digits) {
            if (!isAsciiDigit(digit)) {
                Log.e(TAG, "Non numeric pass code for " + getType());
                return false;
            }
        }
        return true;
    }

    @Override
    public void initBackground() {
    }

    private boolean isAsciiDigit(char c) {
        return '0' <= c && c <= '9';
    }
}