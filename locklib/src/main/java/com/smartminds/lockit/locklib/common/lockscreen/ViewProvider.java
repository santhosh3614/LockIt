package com.smartminds.lockit.locklib.common.lockscreen;

import android.view.View;

/**
 * Created by android on 17/3/15.
 */
public interface ViewProvider<T> {

    View getHeaderView();

    View getChildView();

    void fillHeaderView(View view, String comment);

    void fillChildView(View view, T t,String searchString);

}
