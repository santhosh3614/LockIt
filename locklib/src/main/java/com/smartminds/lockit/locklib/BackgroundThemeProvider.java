package com.smartminds.lockit.locklib;

import android.net.Uri;

import com.smartminds.lockit.locklib.common.lockscreen.background.BackgroundTheme;

import java.util.List;

/**
 * Created by santhoshkumar on 8/5/15.
 */
public interface BackgroundThemeProvider {

    List<BackgroundTheme> getBackgroundThemes();

    BackgroundTheme getSelectedBackgroundTheme();

    BackgroundTheme insertBackgroundTheme(String name,Uri filePath);

    void setBackgroundTheme(BackgroundTheme backgroundTheme);

    void deleteBackgroundTheme(BackgroundTheme backgroundTheme);

}
