package com.smartminds.lockit.locklib.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.smartminds.lockit.locklib.UserProfile;

/**
 * Created by santhoshkumar on 24/4/15.
 */
public class LockItProfile implements UserProfile, Parcelable {

    private long id;
    private String profileName;

    public static final Creator<LockItProfile> CREATOR
            = new Creator<LockItProfile>() {
        public LockItProfile createFromParcel(Parcel in) {
            return new LockItProfile(in);
        }

        public LockItProfile[] newArray(int size) {
            return new LockItProfile[size];
        }
    };

    private LockItProfile(Parcel in) {
        id = in.readLong();
        profileName = in.readString();
    }

    LockItProfile(long id, String profileName) {
        this.id = id;
        this.profileName = profileName;
    }

    @Override
    public String getProfileName() {
        return profileName;
    }

    void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LockItProfile) {
            LockItProfile lockItProfile = (LockItProfile) o;
            return lockItProfile.id == id;
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return profileName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(profileName);
    }

}
