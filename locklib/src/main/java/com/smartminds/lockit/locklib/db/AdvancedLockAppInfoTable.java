package com.smartminds.lockit.locklib.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.appsforbb.common.sqlitelib.ColumnTypes;
import com.appsforbb.common.sqlitelib.ObjectTable;
import com.smartminds.lockit.locklib.BasicLockInfo;

/**
 * Created by santhosh on 14/6/15.ab
 */
public class AdvancedLockAppInfoTable extends ObjectTable<AdvancedAppLock> {

    static final String TABLE_NAME = AdvancedLockAppInfoTable.class.getSimpleName();

    private ColumnTypes.LongColumn PROFILE_ID = new ColumnTypes.LongColumn("profile_id");
    //Advanced lock
    private ColumnTypes.BooleanColumn LOCK_INSTALL_UNINSTALL = new ColumnTypes.BooleanColumn("in_uninstall");
    private ColumnTypes.BooleanColumn LOCK_RECENT_TASK = new ColumnTypes.BooleanColumn("recent_task");
    private ColumnTypes.BooleanColumn LOCK_TASK_MANGER = new ColumnTypes.BooleanColumn("task_manager");
    private ColumnTypes.BooleanColumn LOCK_INCOMMING_CALLS = new ColumnTypes.BooleanColumn("incoming_calls");

    //switch lock
    private ColumnTypes.BooleanColumn LOCK_WIFI = new ColumnTypes.BooleanColumn("wifi");
    private ColumnTypes.BooleanColumn LOCK_BLUETOOTH = new ColumnTypes.BooleanColumn("bluetooth");
    private ColumnTypes.BooleanColumn LOCK_MOBILEDATA = new ColumnTypes.BooleanColumn("mobile_data");
    private ColumnTypes.BooleanColumn LOCK_AUTOSYNC = new ColumnTypes.BooleanColumn("auto_sync");

    protected AdvancedLockAppInfoTable() {
        super(TABLE_NAME);
        addColumns(PROFILE_ID, LOCK_INSTALL_UNINSTALL, LOCK_RECENT_TASK, LOCK_TASK_MANGER,
                LOCK_INCOMMING_CALLS, LOCK_WIFI, LOCK_BLUETOOTH, LOCK_MOBILEDATA, LOCK_AUTOSYNC);
        addRawConstraint("FOREIGN KEY (" + PROFILE_ID + ") REFERENCES '" +
                UserProfileTable.TABLE_NAME + "'(rowid) ON DELETE CASCADE");
    }

    @Override
    protected AdvancedAppLock readRecordRow(long l, Cursor cursor) {

        long userProfileId = PROFILE_ID.getValue(cursor);

        boolean isUnInstallLocked = LOCK_INSTALL_UNINSTALL.getValue(cursor);
        boolean isRecentTaskLocked = LOCK_RECENT_TASK.getValue(cursor);
        boolean isTaskMangerLocked = LOCK_TASK_MANGER.getValue(cursor);
        boolean isIncommingLocked = LOCK_INCOMMING_CALLS.getValue(cursor);

        AdvancedAppLock.AdvancedLocks advancedLocks = new AdvancedAppLock.AdvancedLocks(userProfileId, isUnInstallLocked,
                isRecentTaskLocked, isTaskMangerLocked, isIncommingLocked);

        boolean isWifiLocked = LOCK_WIFI.getValue(cursor);
        boolean isBluetoothLocked = LOCK_BLUETOOTH.getValue(cursor);
        boolean isMobileDataLocked = LOCK_MOBILEDATA.getValue(cursor);
        boolean isAutoSyncLocked = LOCK_AUTOSYNC.getValue(cursor);
        AdvancedAppLock.AdvancedSwitchLocks advancedSwitchLocks = new AdvancedAppLock.AdvancedSwitchLocks(isWifiLocked, isBluetoothLocked,
                isMobileDataLocked, isAutoSyncLocked);
        AdvancedAppLock advancedAppLock = new AdvancedAppLock(userProfileId, advancedLocks, advancedSwitchLocks);
        return advancedAppLock;
    }

    @Override
    protected void fillRecordRow(SQLiteRow sqLiteRow, AdvancedAppLock advancedAppLock) {
        sqLiteRow.setColumnValue(PROFILE_ID, advancedAppLock.getUserProfileId());
        //advanced locks
        AdvancedAppLock.AdvancedLocks advancedLocks = advancedAppLock.getAdvancedLock();
        sqLiteRow.setColumnValue(LOCK_INSTALL_UNINSTALL, isLocked(advancedLocks, AdvancedAppLock.Type.UN_INSTALL));
        sqLiteRow.setColumnValue(LOCK_RECENT_TASK, isLocked(advancedLocks, AdvancedAppLock.Type.RECENTASK));
        sqLiteRow.setColumnValue(LOCK_TASK_MANGER, isLocked(advancedLocks, AdvancedAppLock.Type.TASKMANAGER));
        sqLiteRow.setColumnValue(LOCK_INCOMMING_CALLS, isLocked(advancedLocks, AdvancedAppLock.Type.INCOMMINGCALL));

        //switch locks
        AdvancedAppLock.AdvancedSwitchLocks advancedSwitchLocks = advancedAppLock.getAdvancedSwitchLocks();
        sqLiteRow.setColumnValue(LOCK_WIFI, isLocked(advancedSwitchLocks, AdvancedAppLock.Type.WIFI));
        sqLiteRow.setColumnValue(LOCK_BLUETOOTH, isLocked(advancedSwitchLocks, AdvancedAppLock.Type.BLUETOOTH));
        sqLiteRow.setColumnValue(LOCK_MOBILEDATA, isLocked(advancedSwitchLocks, AdvancedAppLock.Type.MOBILEDATA));
        sqLiteRow.setColumnValue(LOCK_AUTOSYNC, isLocked(advancedSwitchLocks, AdvancedAppLock.Type.AUTOSYNC));
    }

    private boolean isLocked(AdvancedAppLock.AdvancedLocks advancedLocks, AdvancedAppLock.Type type) {
        BasicLockInfo basicLockInfo = advancedLocks.getBasicLockInfo(type);
        return basicLockInfo != null ? basicLockInfo.isLocked() : false;
    }

    private boolean isLocked(AdvancedAppLock.AdvancedSwitchLocks advancedLocks, AdvancedAppLock.Type type) {
        BasicLockInfo basicLockInfo = advancedLocks.getBasicLockInfo(AdvancedAppLock.Type.UN_INSTALL);
        return basicLockInfo != null ? basicLockInfo.isLocked() : false;
    }

    AdvancedAppLock getAdvancedAppLock(long userProfileId) {
        return getSingleRecord(WHERE(PROFILE_ID + "=?", String.valueOf(userProfileId)));
    }

    public void updateBasicAppLock(long userProfileId,boolean isLocked,AdvancedAppLock.Type type){
        ContentValues contentValues=new ContentValues();
        if(type== AdvancedAppLock.Type.WIFI){
            contentValues.put(LOCK_WIFI.getName(),isLocked);
        }else if(type== AdvancedAppLock.Type.BLUETOOTH){
            contentValues.put(LOCK_BLUETOOTH.getName(),isLocked);
        }else if(type== AdvancedAppLock.Type.MOBILEDATA){
            contentValues.put(LOCK_MOBILEDATA.getName(),isLocked);
        }else if(type== AdvancedAppLock.Type.AUTOSYNC){
            contentValues.put(LOCK_AUTOSYNC.getName(),isLocked);
        }else if(type== AdvancedAppLock.Type.INCOMMINGCALL){
            contentValues.put(LOCK_INCOMMING_CALLS.getName(),isLocked);
        }else if(type== AdvancedAppLock.Type.RECENTASK){
            contentValues.put(LOCK_RECENT_TASK.getName(),isLocked);
        }else if(type== AdvancedAppLock.Type.TASKMANAGER){
            contentValues.put(LOCK_TASK_MANGER.getName(),isLocked);
        }else if(type== AdvancedAppLock.Type.UN_INSTALL){
            contentValues.put(LOCK_INSTALL_UNINSTALL.getName(),isLocked);
        }
        update(contentValues,WHERE(PROFILE_ID+"=?",userProfileId));
    }

    long addAdvancedAppLock(AdvancedAppLock advancedAppLock) {
        return super.insertNewRecord(advancedAppLock);
    }


    public void deleteLockAppInfo(AdvancedAppLock advancedAppLock) {
        delete(WHERE(PROFILE_ID + " = ?", String.valueOf(advancedAppLock.getUserProfileId())));
    }

}
