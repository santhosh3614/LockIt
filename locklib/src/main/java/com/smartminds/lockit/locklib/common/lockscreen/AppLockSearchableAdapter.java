package com.smartminds.lockit.locklib.common.lockscreen;

import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

/**
 * Created by android on 17/3/15.
 */
public interface AppLockSearchableAdapter<T> extends ListAdapter,SpinnerAdapter,Filterable {

    public abstract void lockApp(T t, boolean locked);

    public abstract void setViewProvider(ViewProvider viewProvider);

    public abstract int getViewTypeCount();

}
