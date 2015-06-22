package com.smartminds.lockit.locklib.db;

import android.annotation.TargetApi;
import android.os.Build;

import com.appsforbb.common.sqlitelib.SQLiteStore;
import com.smartminds.lockit.locklib.common.lockscreen.background.BackgroundThemeTable;

/**
 * Created by santhoshkumar on 24/4/15.
 */
public class Db {

    private static final String DATABASE_NAME = "applocklib.db";
    private static final int DATABASE_VERSION = 1;
    private UserProfileTable userProfileTable = new UserProfileTable();
    private AppInfoTable appInfoTable = new AppInfoTable();
    private AdvancedLockAppInfoTable advancedLockAppInfoTable = new AdvancedLockAppInfoTable();
    private BackgroundThemeTable bgThemeTable = new BackgroundThemeTable();
    private SQLiteStore store;
    private static Db db;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public Db() {
        store = SQLiteStore.getInstance(DATABASE_NAME, DATABASE_VERSION, userProfileTable,
                advancedLockAppInfoTable, appInfoTable, bgThemeTable);
        store.getDatabase().setForeignKeyConstraintsEnabled(true);
    }

    protected AppInfoTable getAppInfoTable() {
        return appInfoTable;
    }

    protected UserProfileTable getUserProfileTable() {
        return userProfileTable;
    }


    public BackgroundThemeTable getBgThemeTable() {
        return bgThemeTable;
    }

    protected SQLiteStore getStore() {
        return store;
    }

    public static synchronized Db getInstance() {
        if (db == null) {
            db = new Db();
        }
        return db;
    }

    public AdvancedLockAppInfoTable getAdvancedLockAppInfoTable() {
        return advancedLockAppInfoTable;
    }
}
