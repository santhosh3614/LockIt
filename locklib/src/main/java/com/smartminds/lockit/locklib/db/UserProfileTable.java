package com.smartminds.lockit.locklib.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.appsforbb.common.sqlitelib.ColumnTypes;
import com.appsforbb.common.sqlitelib.ObjectTable;
import com.smartminds.lockit.locklib.UserProfile;

import java.util.ArrayList;

/**
 * Created by santhoshkumar on 24/4/15.
 */
class UserProfileTable extends ObjectTable<LockItProfile> {

    static final String TABLE_NAME = UserProfileTable.class.getSimpleName();
    private ColumnTypes.StringColumn PROFILE_NAME = new ColumnTypes.StringColumn("profile_name");

    protected UserProfileTable() {
        super(TABLE_NAME);
        addColumns(PROFILE_NAME);
    }

    @Override
    protected LockItProfile readRecordRow(long rowid, Cursor c) {
        return new LockItProfile(rowid, PROFILE_NAME.getValue(c));
    }

    @Override
    protected void fillRecordRow(SQLiteRow r, LockItProfile userProfile) {
        r.setColumnValue(PROFILE_NAME, userProfile.getProfileName());
    }

    ArrayList<LockItProfile> getAllProfiles() {
        return super.getAllRecords(ORDERBY(PROFILE_NAME));
    }

    UserProfile getProfile(long profilerId) {
        return getRecordById(profilerId);
    }

    void deleteProfile(long profileId) {
        deleteByRowId(profileId);
    }

    long addDefaultProfile(String profileName) {
        long profileId = insertOnDuplicateKeyReplace(new LockItProfile(-1, profileName));
        if (profileId != -1) {
            AdvancedAppLock.AdvancedLocks advancedLocks = new AdvancedAppLock.AdvancedLocks(profileId,
                    AdvancedAppLock.AdvancedLocks.DEFAULT_LOCK_UNINSTALL, AdvancedAppLock.AdvancedLocks.DEFAULT_LOCK_RECENTTASK,
                    AdvancedAppLock.AdvancedLocks.DEFAULT_LOCK_TASKMANGER, AdvancedAppLock.AdvancedLocks.DEFAULT_LOCK_INCOMMING);

            AdvancedAppLock.AdvancedSwitchLocks advancedSwitchLocks = new AdvancedAppLock.AdvancedSwitchLocks(
                    AdvancedAppLock.AdvancedSwitchLocks.DEFAULT_LOCK_WIFI, AdvancedAppLock.AdvancedSwitchLocks.DEFAULT_LOCK_BLUETOOTH,
                    AdvancedAppLock.AdvancedSwitchLocks.DEFAULT_LOCK_MOBILDATA, AdvancedAppLock.AdvancedSwitchLocks.DEFAULT_LOCK_AUTOSYNC);
            AdvancedAppLock advancedAppLock = new AdvancedAppLock(profileId, advancedLocks, advancedSwitchLocks);
            Db.getInstance().getAdvancedLockAppInfoTable().addAdvancedAppLock(advancedAppLock);
        }
        return profileId;
    }

    void updateProfile(LockItProfile userProfile) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_NAME.toString(), userProfile.getProfileName());
        updateByRowId(contentValues, userProfile.getId());
    }

}
