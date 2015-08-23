package com.smartminds.lockit.locklib.db;

import android.database.Cursor;

import com.appsforbb.common.sqlitelib.ColumnTypes;
import com.appsforbb.common.sqlitelib.ObjectTable;
import com.smartminds.lockit.locklib.UserProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class AppInfoTable extends ObjectTable<AppInfoTable.LockInfo> {

    private static final String TABLE_NAME = "AppInfoTable";
    private ColumnTypes.LongColumn PROFILE_ID = new ColumnTypes.LongColumn("profile_id");
    private ColumnTypes.StringColumn PKG_NAME = new ColumnTypes.StringColumn("pkg_name");

    protected AppInfoTable() {
        super(TABLE_NAME);
        addColumns(PKG_NAME, PROFILE_ID);
        addUniqueConstraint(PKG_NAME, PROFILE_ID);
        addRawConstraint("FOREIGN KEY (" + PROFILE_ID + ") REFERENCES '" + UserProfileTable.TABLE_NAME + "'(rowid) ON DELETE CASCADE");
    }

    @Override
    protected LockInfo readRecordRow(long rowId, Cursor cursor) {
        LockInfo lockInfo = new LockInfo();
        lockInfo.profileId = PROFILE_ID.getValue(cursor);
        lockInfo.pkgName = PKG_NAME.getValue(cursor);
        return lockInfo;
    }

    @Override
    protected void fillRecordRow(SQLiteRow sqLiteRow, LockInfo lockInfo) {
        sqLiteRow.setColumnValue(PKG_NAME, lockInfo.pkgName);
        sqLiteRow.setColumnValue(PROFILE_ID, lockInfo.profileId);
    }

    String getSingleRecord(String pkgName, long profileId) {
        LockInfo lockInfo = super.getSingleRecord(WHERE(PKG_NAME + "=? and " + PROFILE_ID + " = ?", pkgName,
                String.valueOf(profileId)));
        return lockInfo != null ? lockInfo.pkgName : null;
    }

    long insertLockedApp(String pkgName, UserProfile userProfile) {

        System.out.println("insertLockedApp...." + userProfile);

        LockInfo lockInfo = new LockInfo();
        lockInfo.profileId = ((LockItProfile) userProfile).getId();
        lockInfo.pkgName = pkgName;
        return super.insertOnDuplicateKeyReplace(lockInfo);
    }

    public void deleteLockAppInfo(String packageName, UserProfile userProfile) {
        delete(WHERE(PKG_NAME + "=? and " + PROFILE_ID + " = ?", packageName,
                String.valueOf(((LockItProfile) userProfile).getId())));
    }

    protected List<String> getAllLockedPkgs(UserProfile lockItProfile) {
        if (lockItProfile != null) {
            List<LockInfo> lockInfos = super.getRecords(WHERE(PROFILE_ID + "=? ", String.valueOf(((LockItProfile) lockItProfile).getId())));
            List<String> list = new ArrayList<>();
            for (LockInfo lockInfo : lockInfos) {
                list.add(lockInfo.pkgName);
            }
            return list;
        } else {
            return (List<String>) Collections.EMPTY_LIST;
        }
    }

    static class LockInfo {
        long profileId;
        String pkgName;
    }
}
