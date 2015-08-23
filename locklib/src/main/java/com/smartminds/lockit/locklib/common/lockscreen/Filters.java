package com.smartminds.lockit.locklib.common.lockscreen;

/**
 * Created by santhosh on 25/6/15.
 */
public interface Filters {

    public static enum Filter {
        ALL, LOCKED, UNLOCKED
    }

    public static enum SortOrder {
        NAME, LOCKED_FIRST, UNLOCKED_FIRST
    }

}
