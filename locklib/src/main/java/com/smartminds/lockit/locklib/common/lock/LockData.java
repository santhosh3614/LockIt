package com.smartminds.lockit.locklib.common.lock;

import java.io.Serializable;

public abstract class LockData implements Serializable {

    public abstract boolean matches(LockData data);
}
