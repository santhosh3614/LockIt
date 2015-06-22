package com.smartminds.lockit.locklib.common.lockscreen.background;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.appsforbb.common.appbase.AppBase;
import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.BackgroundThemeProvider;
import com.smartminds.lockit.locklib.LockItSettings;
import com.smartminds.lockit.locklib.db.Db;
import com.smartminds.lockit.locklib.services.AppLockService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by santhoshkumar on 8/5/15.
 */
public class BackgroundThemeProviderImpl implements BackgroundThemeProvider {

    private LockItSettings settings = new LockItSettings(AppLockLib.class.getName());

    private static final String BG_THEME_IMAGES_FOLDER_NAME = "lockit_bg_images";

    public BackgroundThemeProviderImpl() {
        Db db = Db.getInstance();
        BackgroundThemeTable bgThemeTable = db.getBgThemeTable();
        List<BackgroundTheme> backgroundThemes = bgThemeTable.getBackgroundThemes();
        if (backgroundThemes.size() == 0) {
            insertDefaultbackgrounds(bgThemeTable);
        }
    }

    private void insertDefaultbackgrounds(BackgroundThemeTable bgThemeTable) {
        Context context = AppBase.getAppContext();
        //insert default background
    }

    @Override
    public List<BackgroundTheme> getBackgroundThemes() {
        Db db = Db.getInstance();
        BackgroundThemeTable bgThemeTable = db.getBgThemeTable();
        return bgThemeTable.getBackgroundThemes();
    }

    @Override
    public BackgroundTheme getSelectedBackgroundTheme() {
        Db db = Db.getInstance();
        BackgroundThemeTable bgThemeTable = db.getBgThemeTable();
        return bgThemeTable.getBackgroundTheme(settings.getLong(LockItSettings.BG_THEME_ID, 1));
    }

    @Override
    public BackgroundTheme insertBackgroundTheme(String name, Uri filePath) {
        Db db = Db.getInstance();
        BackgroundThemeTable bgThemeTable = db.getBgThemeTable();
        //TODO copy file to internal loation
        File internalImageStorage = AppBase.getAppContext().getDir(BG_THEME_IMAGES_FOLDER_NAME, Context.MODE_PRIVATE);
        if (!internalImageStorage.exists()) {
            internalImageStorage.mkdir();
        }
        final File imgFile = new File(internalImageStorage + "/custom" + System.currentTimeMillis() + "_port.png");
        BackgroundTheme backgroundTheme = new BackgroundTheme(-1, getFileName(filePath.getPath()),
                imgFile.getAbsolutePath());
        backgroundTheme.setId(bgThemeTable.insertBackground(backgroundTheme));
        copyImage(filePath.getPath(), imgFile.getAbsolutePath());
        return backgroundTheme;
    }

    private void copyImage(String originalPath, String duplicatePath) {
        try {
            InputStream in = new FileInputStream(originalPath);
            OutputStream out = new FileOutputStream(duplicatePath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    private String getFileName(String filepath) {
        return filepath.substring(filepath.lastIndexOf("/") + 1);
    }

    @Override
    public void setBackgroundTheme(BackgroundTheme backgroundTheme) {
        settings.setLong(LockItSettings.BG_THEME_ID, backgroundTheme.getId());
        LocalBroadcastManager.getInstance(AppBase.getAppContext()).sendBroadcast(
                new Intent(AppLockService.ACTION_CHANGE_LOCK_BG));
    }

    @Override
    public void deleteBackgroundTheme(BackgroundTheme backgroundTheme) {
        Db db = Db.getInstance();
        db.getBgThemeTable().deleteBackgroundTheme(backgroundTheme);
    }
}
