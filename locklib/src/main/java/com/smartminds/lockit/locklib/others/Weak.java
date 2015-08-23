package com.smartminds.lockit.locklib.others;

import java.lang.ref.WeakReference;

public abstract class Weak<T> {
    private WeakReference<T> wr;

    protected abstract T load();

    public T get() {
        T t = wr == null ? null : wr.get();

        if (t == null) {
            t = load();
            wr = new WeakReference<T>(t);
        }
        return t;
    }
}
