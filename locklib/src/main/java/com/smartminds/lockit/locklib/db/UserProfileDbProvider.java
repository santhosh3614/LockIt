package com.smartminds.lockit.locklib.db;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.LockItSettings;
import com.smartminds.lockit.locklib.UserProfile;
import com.smartminds.lockit.locklib.UserProfileProvider;

import java.util.List;

import static com.appsforbb.common.appbase.AppBase.getAppContext;
import static com.smartminds.lockit.locklib.LockItSettings.PROFILE_ID;
import static com.smartminds.lockit.locklib.services.BasicLockService.ACTION_LOCK_PROFILE_CHANGED;

/**
 * Created by santhoshkumar on 24/4/15.
 */
public class UserProfileDbProvider implements UserProfileProvider {

    private LockItSettings setting = new LockItSettings(AppLockLib.class.getName());

    private static final String DEFAULT_PROFILE_NAME = "Home";

    public UserProfileDbProvider() {
        Db db = Db.getInstance();
        UserProfileTable userProfileTable = db.getUserProfileTable();
        if (userProfileTable.getAllProfiles().size() == 0) {
            long id = userProfileTable.addDefaultProfile(DEFAULT_PROFILE_NAME);
            setting.setLong(PROFILE_ID, id);
        }
    }

    @Override
    public UserProfile[] getAllUserProfiles() {
        Db db = Db.getInstance();
        List<LockItProfile> userProfiles = db.getUserProfileTable().getAllProfiles();
        return userProfiles.toArray(new UserProfile[userProfiles.size()]);
    }

    @Override
    public void enableProfile(UserProfile userProfile) {
        setting.setLong(PROFILE_ID, ((LockItProfile) userProfile).getId());
        LocalBroadcastManager.getInstance(getAppContext()).sendBroadcast(
                new Intent(ACTION_LOCK_PROFILE_CHANGED));
    }

    @Override
    public UserProfile addProfile(String profileName) {
        long id = Db.getInstance().getUserProfileTable().addDefaultProfile(profileName);
        return new LockItProfile(id, profileName);
    }

    @Override
    public void updateUserProfile(UserProfile profile, String profileName) {
        LockItProfile lockItProfile = (LockItProfile) profile;
        lockItProfile.setProfileName(profileName);
        Db.getInstance().getUserProfileTable().updateProfile(lockItProfile);
    }

    @Override
    public void removeProfile(UserProfile userProfile) {
        Db db = Db.getInstance();
        db.getUserProfileTable().deleteProfile(((LockItProfile) userProfile).getId());
    }

    @Override
    public UserProfile getProfile() {
        Db db = Db.getInstance();
        return db.getUserProfileTable().getProfile(setting.getLong(PROFILE_ID, 1));
    }
}
