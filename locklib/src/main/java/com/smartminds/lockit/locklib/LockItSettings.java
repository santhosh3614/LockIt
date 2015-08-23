package com.smartminds.lockit.locklib;

import com.appsforbb.common.annotations.NonNull;
import com.appsforbb.common.settings.AppSettings;

final public class LockItSettings extends AppSettings{

    public static final String LOGIN_ACTIVITY="LOGIN_ACTIVITY";
    public static final String IS_LOCKIT_ENABLED ="ENABLED";
    public static final String IS_LOCK_UNINSTALL_UNINSTALL ="LOCK_INSTALL_UNINSTALL";
    public static final String IS_LOCK_SETTING_APP ="LOCK_SETTINGS";
    public static final String IS_LOCK_RECENT_APP ="LOCK_RECENT";
    public static final String RECOVERY_EMAIL="RECOVERY_EMAIL";
    public static final String IS_LOCK_WIFI ="IS_LOCK_WIFI";
    public static final String IS_LOCK_BLUETOOTH = "IS_LOCK_BLUETOOTH";
    public static final String IS_LOCK_MOBILE_DATA = "IS_LOCK_MOBILE_DATA";
    public static final String IS_AUTO_SYNC_LOCKED = "LOCK_AUTO_SYNC";
    public static final String PROFILE_ID="profile_id";
    public static final String BG_THEME_ID = "bg_theme_id";
    public static final String NUM_LOCK_KEY="num_lock";
    public static final String CHAR_LOCK_KEY ="char_lock";
    public static final String NUM_LOCK_HINT = "num_lock_hint";
    public static final String CHAR_LOCK_HINT = "char_lock_hint";

    public LockItSettings(@NonNull String className) {
        super(className);
    }
}
