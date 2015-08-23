package com.smartminds.lockit.locklib;

public interface UserProfileProvider {

    public UserProfile[] getAllUserProfiles();

    public UserProfile addProfile(String profileName);

    public void updateUserProfile(UserProfile profile, String profilerName);

    public void removeProfile(UserProfile userProfile);

    public void enableProfile(UserProfile userProfile);

    public UserProfile getProfile();

}
