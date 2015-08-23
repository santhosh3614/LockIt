package com.smartminds.lockit.locklib.appinfo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.appsforbb.common.appbase.AppBase;

import java.util.ArrayList;
import java.util.List;

public class AppInfoProvider {

    private static PackageManager pm = AppBase.getAppContext().getPackageManager();

    /**
     * @return
     */
    public static LaunchableAppInfo[] getAllLauncherApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> appInfoList = pm.queryIntentActivities(intent, 0);
        List<LaunchableAppInfo> launchableAppInfos = new ArrayList<LaunchableAppInfo>();
        for (ResolveInfo appInfo : appInfoList) {
            LaunchableAppInfo appInfos = new LaunchableAppInfo(appInfo.activityInfo);
            launchableAppInfos.add(appInfos);
        }
        return launchableAppInfos.toArray(new LaunchableAppInfo[launchableAppInfos.size()]);
    }

    /**
     * @return
     */
    public static LaunchableAppInfo[] getAllLaunchableApps(boolean excludeLauncherApps, boolean excludeOwnApp) {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appInfoList = pm.queryIntentActivities(launcherIntent, 0);
        List<LaunchableAppInfo> launchableAppInfos = new ArrayList<LaunchableAppInfo>();
        for (ResolveInfo appInfo : appInfoList) {
            LaunchableAppInfo launchableAppInfo = new LaunchableAppInfo(appInfo.activityInfo);
            if (launchableAppInfo.getPackageName().equals(AppBase.getAppInfo().packageName) && excludeOwnApp) {
                continue;
            }
            launchableAppInfos.add(launchableAppInfo);
        }
        if (excludeLauncherApps) {
            LaunchableAppInfo[] launcherAppInfos = getAllLauncherApps();
            for (LaunchableAppInfo launcherAppInfo : launcherAppInfos) {
                if (launchableAppInfos.contains(launcherAppInfo)) {
                    launchableAppInfos.remove(launcherAppInfo);
                }
            }
        }
        return launchableAppInfos.toArray(new LaunchableAppInfo[launchableAppInfos.size()]);
    }

    /**
     * @param packageName
     * @return
     */
    public static LaunchableAppInfo[] getAllLaunchableAppsByPkg(String packageName) {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launcherIntent.setPackage(packageName);
        List<ResolveInfo> appInfoList = pm.queryIntentActivities(launcherIntent, 0);
        List<LaunchableAppInfo> launchableAppInfos = new ArrayList<LaunchableAppInfo>();
        for (ResolveInfo appInfo : appInfoList) {
            LaunchableAppInfo appInfos = new LaunchableAppInfo(appInfo.activityInfo);
            launchableAppInfos.add(appInfos);
        }
        return launchableAppInfos.toArray(new LaunchableAppInfo[launchableAppInfos.size()]);
    }

    /**
     * @param componentName
     * @return
     */
    public static LaunchableAppInfo getLaunchableAppInfo(ComponentName componentName) {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launcherIntent.setComponent(componentName);
        List<ResolveInfo> appInfoList = pm.queryIntentActivities(launcherIntent, 0);
        List<LaunchableAppInfo> launchableAppInfos = new ArrayList<LaunchableAppInfo>();
        LaunchableAppInfo launchableAppInfo = null;
        for (ResolveInfo appInfo : appInfoList) {
            LaunchableAppInfo appInfos = new LaunchableAppInfo(appInfo.activityInfo);
            launchableAppInfo = appInfos;
            launchableAppInfos.add(appInfos);
        }
        if (launchableAppInfos.size() > 1) {
            throw new UnsupportedOperationException("more than one launcher");
        }
        return launchableAppInfo;
    }
}