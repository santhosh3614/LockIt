package com.smartminds.lockit.locklib.common.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import com.appsforbb.common.annotations.NonNull;

public abstract class CustomView extends View {
    private boolean flag;

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);
    }

    private static class SavedState extends BaseSavedState {
        private Bundle state;
        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            state = (Bundle) in.readBundle();
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBundle(state);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public final Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        flag = false;
        onSaveInstanceStateCustom(ss.state);
        if(!flag)
            throw new IllegalStateException("super.onSaveInstanceStateCustom() was not called.");
        return ss;
    }

    protected void onSaveInstanceStateCustom(Bundle state) {
        flag = true;
    }

    @Override
    public final void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        flag = false;
        onRestoreInstanceStateCustom(ss.state);
        if(!flag)
            throw new IllegalStateException("super.onRestoreInstanceStateCustom() was not called.");
        requestLayout();
    }

    protected void onRestoreInstanceStateCustom(Bundle state) {
        flag = true;
    }
}
