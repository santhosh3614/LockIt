package com.smartminds.lockit.locklib.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.appsforbb.common.sqlitelib.ColumnTypes;
import com.appsforbb.common.sqlitelib.ColumnTypes.BooleanColumn;
import com.appsforbb.common.sqlitelib.ObjectTable;
import com.smartminds.lockit.locklib.BasicLockInfo;
import com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedLocks;
import com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedLocks.AdvancedAppBasicLock;
import com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedSwitchLocks;
import com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedSwitchLocks.AdvancedAppSwitchBasicLock;
import com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedSwitchLocks.Type;

import java.util.EnumMap;

import static com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedLocks.Type.INCOMMINGCALL;
import static com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedLocks.Type.RECENTASK;
import static com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedLocks.Type.TASKMANAGER;
import static com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedLocks.Type.UN_INSTALL;
import static com.smartminds.lockit.locklib.db.AdvancedAppLock.AdvancedSwitchLocks.Type.*;

/**
 * Created by santhosh on 14/6/15.ab
 */
public class AdvancedLockAppInfoTable extends ObjectTable<AdvancedAppLock> {

    static final String TABLE_NAME = AdvancedLockAppInfoTable.class.getSimpleName();

    private ColumnTypes.LongColumn PROFILE_ID = new ColumnTypes.LongColumn("profile_id");

    private EnumMap<AdvancedLocks.Type, BooleanColumn> advancedAppEnum =
            new EnumMap<AdvancedLocks.Type, BooleanColumn>
                    (AdvancedLocks.Type.class);
    private BooleanColumn LOCK_INSTALL_UNINSTALL = new BooleanColumn("in_uninstall");
    private BooleanColumn LOCK_RECENT_TASK = new BooleanColumn("recent_task");
    private BooleanColumn LOCK_TASK_MANGER = new BooleanColumn("task_manager");
    private BooleanColumn LOCK_INCOMMING_CALLS = new BooleanColumn("incoming_calls");

    //Advanced lock

    //switch lock
    private EnumMap<Type, BooleanColumn> advancedAppSwitchEnum =
            new EnumMap<Type, BooleanColumn>
                    (Type.class);
    private BooleanColumn LOCK_WIFI = new BooleanColumn("wifi");
    private BooleanColumn LOCK_BLUETOOTH = new BooleanColumn("bluetooth");
    private BooleanColumn LOCK_MOBILEDATA = new BooleanColumn("mobile_data");
    private BooleanColumn LOCK_AUTOSYNC = new BooleanColumn("auto_sync");


    protected AdvancedLockAppInfoTable() {
        super(TABLE_NAME);
        addColumns(PROFILE_ID,
                LOCK_INSTALL_UNINSTALL, LOCK_RECENT_TASK, LOCK_TASK_MANGER,LOCK_INCOMMING_CALLS,
                LOCK_WIFI, LOCK_BLUETOOTH, LOCK_MOBILEDATA, LOCK_AUTOSYNC);
        addRawConstraint("FOREIGN KEY (" + PROFILE_ID + ") REFERENCES '" +
                UserProfileTable.TABLE_NAME + "'(rowid) ON DELETE CASCADE");

        advancedAppEnum.put(INCOMMINGCALL, LOCK_INCOMMING_CALLS);
        advancedAppEnum.put(TASKMANAGER, LOCK_TASK_MANGER);
        advancedAppEnum.put(RECENTASK, LOCK_RECENT_TASK);
        advancedAppEnum.put(UN_INSTALL, LOCK_INSTALL_UNINSTALL);

        advancedAppSwitchEnum.put(WIFI, LOCK_WIFI);
        advancedAppSwitchEnum.put(BLUETOOTH, LOCK_BLUETOOTH);
        advancedAppSwitchEnum.put(MOBILEDATA, LOCK_MOBILEDATA);
        advancedAppSwitchEnum.put(AUTOSYNC, LOCK_AUTOSYNC);


    }

    @Override
    protected AdvancedAppLock readRecordRow(long l, Cursor cursor) {

        long userProfileId = PROFILE_ID.getValue(cursor);

        boolean isUnInstallLocked = LOCK_INSTALL_UNINSTALL.getValue(cursor);
        boolean isRecentTaskLocked = LOCK_RECENT_TASK.getValue(cursor);
        boolean isTaskMangerLocked = LOCK_TASK_MANGER.getValue(cursor);
        boolean isIncommingLocked = LOCK_INCOMMING_CALLS.getValue(cursor);

        AdvancedLocks advancedLocks = new AdvancedLocks(userProfileId, isUnInstallLocked,
                isRecentTaskLocked, isTaskMangerLocked, isIncommingLocked);

        boolean isWifiLocked = LOCK_WIFI.getValue(cursor);
        boolean isBluetoothLocked = LOCK_BLUETOOTH.getValue(cursor);
        boolean isMobileDataLocked = LOCK_MOBILEDATA.getValue(cursor);
        boolean isAutoSyncLocked = LOCK_AUTOSYNC.getValue(cursor);
        AdvancedSwitchLocks advancedSwitchLocks = new AdvancedSwitchLocks(isWifiLocked, isBluetoothLocked,
                isMobileDataLocked, isAutoSyncLocked);
        AdvancedAppLock advancedAppLock = new AdvancedAppLock(userProfileId, advancedLocks, advancedSwitchLocks);
        return advancedAppLock;
    }

    @Override
    protected void fillRecordRow(SQLiteRow sqLiteRow, AdvancedAppLock advancedAppLock) {
        sqLiteRow.setColumnValue(PROFILE_ID, advancedAppLock.getUserProfileId());
        //advanced locks
        AdvancedLocks advancedLocks = advancedAppLock.getAdvancedLock();
        for (AdvancedLocks.Type type : AdvancedLocks.Type.values()) {
            BasicLockInfo basicLockInfo = advancedLocks.getBasicLockInfo(type);
            BooleanColumn booleanColumn = advancedAppEnum.get(type);
            System.out.println("Type...."+type+" Col:"+booleanColumn+" BasicLock:"+basicLockInfo);
            if (booleanColumn != null) {
                sqLiteRow.setColumnValue(booleanColumn, basicLockInfo.isLocked());
            }
        }
        //advancedswitch locks
        AdvancedSwitchLocks advancedSwitchLocks = advancedAppLock.getAdvancedSwitchLocks();
        for (Type type : values()) {
            BasicLockInfo basicLockInfo = advancedSwitchLocks.getBasicLockInfo(type);
            BooleanColumn booleanColumn = advancedAppSwitchEnum.get(type);
            if (basicLockInfo != null && booleanColumn != null) {
                sqLiteRow.setColumnValue(booleanColumn, basicLockInfo.isLocked());
            }
        }
    }

    public AdvancedAppLock getAdvancedAppLock(long userProfileId) {
        return getSingleRecord(WHERE(PROFILE_ID + "=?", String.valueOf(userProfileId)));
    }

    public void updateBasicAppLock(long userProfileId, boolean isLocked, BasicLockInfo basicLockInfo) {
        ContentValues contentValues = new ContentValues();
        if (basicLockInfo instanceof AdvancedAppBasicLock) {
            AdvancedAppBasicLock advancedAppBasicLock =
                    (AdvancedAppBasicLock) basicLockInfo;
            contentValues.put(advancedAppEnum.get(advancedAppBasicLock.getType()).toString(), advancedAppBasicLock.isLocked());
        } else if (basicLockInfo instanceof AdvancedAppSwitchBasicLock) {
            AdvancedAppSwitchBasicLock advancedAppSwitchBasicLock =
                    (AdvancedAppSwitchBasicLock) basicLockInfo;
            contentValues.put(advancedAppSwitchEnum.get(advancedAppSwitchBasicLock.getType()).toString(),
                    advancedAppSwitchBasicLock.isLocked());
        }
        update(contentValues, WHERE(PROFILE_ID + "=?", userProfileId));
    }

    long addAdvancedAppLock(AdvancedAppLock advancedAppLock) {
        return super.insertNewRecord(advancedAppLock);
    }

    public void deleteLockAppInfo(AdvancedAppLock advancedAppLock) {
        delete(WHERE(PROFILE_ID + " = ?", String.valueOf(advancedAppLock.getUserProfileId())));
    }

}
