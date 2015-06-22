package com.smartminds.lockit.locklib.common.lock;

import com.appsforbb.common.annotations.NonNull;
import com.appsforbb.common.annotations.Nullable;

import java.io.Serializable;

public class WordLockData extends LockData implements Serializable {
    @NonNull
    final String code;
    @Nullable
    final String hint;

    public WordLockData(@NonNull String code) {
        this(code, null);
    }

    public WordLockData(@NonNull String code, @Nullable String hint) {
        this.code = code;
        this.hint = hint;
    }

    public boolean hasHint() {
        return hint != null;
    }

    @Nullable
    public String getHint() {
        return hint;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    @Override
    public boolean matches(LockData data) {
        if (data instanceof WordLockData)
            return code.equals(((WordLockData) data).code);
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WordLockData) {
            WordLockData data = (WordLockData)o;
            return code.equals(data.code);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }
}
