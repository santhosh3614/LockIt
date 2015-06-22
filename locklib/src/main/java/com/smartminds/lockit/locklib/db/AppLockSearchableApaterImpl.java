package com.smartminds.lockit.locklib.db;

import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.appsforbb.common.appbase.AppBase;
import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.BasicLockInfo;
import com.smartminds.lockit.locklib.LockAppInfo;
import com.smartminds.lockit.locklib.LockAppListProvider;
import com.smartminds.lockit.locklib.R;
import com.smartminds.lockit.locklib.UserProfile;
import com.smartminds.lockit.locklib.common.lockscreen.AppLockSearchableAdapter;
import com.smartminds.lockit.locklib.common.lockscreen.ViewProvider;
import com.smartminds.lockit.locklib.others.MultiHeaderAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by android on 23/3/15.
 */
class AppLockSearchableApaterImpl extends MultiHeaderAdapter<BasicLockInfo> implements AppLockSearchableAdapter<BasicLockInfo> {

    private final String TAG = AppLockSearchableApaterImpl.class.getSimpleName();
    private String constraint = "";
    private final LockAppListProvider appListProvider;

    private List<BasicLockInfo> totAppInfos;
    private List<BasicLockInfo> advancedApptotAppinfos;
    private List<BasicLockInfo> advancedSwitchtotAppinfos;

    private List<BasicLockInfo>[] tmpApps = new List[3];

    private String[] headerTtles = {AppBase.getAppContext().getString(R.string.advanced_lock_label), AppBase.getAppContext().getString(R.string.additional_lock_label), AppBase.getAppContext().getString(R.string.app_lock_label)};


    private SparseArray<List<BasicLockInfo>> apps = new SparseArray<List<BasicLockInfo>>();

    private ViewProvider<BasicLockInfo> viewProvider;
    private UserProfile userProfile;
    private LockAppListProvider.Filter filter;
    private LockAppListProvider.SortOrder sortOrder;
    private LockAppListProviderImpl.LockComparator lockComparator;


    AppLockSearchableApaterImpl(UserProfile userProfile,
                                LockAppListProvider.Filter filter, LockAppListProvider.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        apps.put(0, new ArrayList<BasicLockInfo>());
        apps.put(1, new ArrayList<BasicLockInfo>());
        apps.put(2, new ArrayList<BasicLockInfo>());
        lockComparator = new LockAppListProviderImpl.LockComparator(sortOrder);
        appListProvider = AppLockLib.getInstance().getAppListProvider();
        setUserProfile(userProfile, filter);
        setFilter(filter);
        initSearchApps();
    }

    public void setUserProfile(UserProfile userProfile, LockAppListProvider.Filter filter) {
        this.userProfile = userProfile;
        AdvancedAppLock advancedLock = appListProvider.getAdvancedLock(userProfile);
        BasicLockInfo[] appListInfo = appListProvider.getAppListInfo(userProfile, filter, sortOrder);
        totAppInfos = Arrays.asList(appListInfo);
        totAppInfos.remove(advancedLock.getAdvancedLock().getBasicLockInfo(AdvancedAppLock.Type.SETTINGS));
        totAppInfos.remove(advancedLock.getAdvancedLock().getBasicLockInfo(AdvancedAppLock.Type.PLAYSTORE));
        Collections.sort(totAppInfos, lockComparator);
        advancedApptotAppinfos = advancedLock.getAdvancedLock().getBasicLocks();
        Collections.sort(advancedApptotAppinfos, lockComparator);
        advancedSwitchtotAppinfos = advancedLock.getAdvancedSwitchLocks().getBasicLocks();
        Collections.sort(advancedSwitchtotAppinfos, lockComparator);
    }

    private List<BasicLockInfo> getAppInfos(List<BasicLockInfo> basicLockApps, boolean isLocked) {
        List<BasicLockInfo> lockedAppInfos = new ArrayList<>();
        for (BasicLockInfo appInfo : basicLockApps) {
            if (appInfo.isLocked() == isLocked) {
                lockedAppInfos.add(appInfo);
            }
        }
        return lockedAppInfos;
    }

    @Override
    public int getHeaderCount() {
        return apps.size();
    }

    @Override
    public List<BasicLockInfo> getHeaderItems(int headerIdx) {
        return apps.get(headerIdx);
    }

    @Override
    public void bindHeaderView(int headerIdx, View convertView, ViewGroup parent) {
        viewProvider.fillHeaderView(convertView, headerTtles[headerIdx]);
    }

    @Override
    public View getHeaderView(int headerIdx, View convertView, ViewGroup parent) {
        return viewProvider.getHeaderView();
    }

    @Override
    public void bindNormalView(BasicLockInfo basicLockInfo, View convertView, ViewGroup parent) {
        viewProvider.fillChildView(convertView, basicLockInfo);
    }

    @Override
    public View getNormalView(BasicLockInfo basicLockInfo, View convertView, ViewGroup parent) {
        return viewProvider.getChildView();
    }

    @Override
    public void lockApp(BasicLockInfo basicLockInfo, boolean locked) {
        if (basicLockInfo instanceof LockAppInfoImpl) {
            LockAppInfo lockAppInfo = (LockAppInfo) basicLockInfo;
            String pkgName = lockAppInfo.getPackageName();
            List<BasicLockInfo> basicLockInfos = tmpApps[2];
            for (BasicLockInfo basicLockAppInfo : basicLockInfos) {
                if (pkgName.equals(lockAppInfo.getPackageName())) {
                    appListProvider.lockApp(lockAppInfo, locked, userProfile);
                    basicLockAppInfo.setLock(locked);
                    if ((filter == LockAppListProvider.Filter.LOCKED && !locked) ||
                            (filter == LockAppListProvider.Filter.UNLOCKED && locked)){
                        apps.get(2).remove(basicLockAppInfo);
                        basicLockInfos.remove(basicLockAppInfo);
                    }
                }
            }
        } else if (basicLockInfo instanceof BasicLockInfoImpl) {
            appListProvider.lockApp(basicLockInfo, locked, userProfile);
            basicLockInfo.setLock(locked);
            if ((filter == LockAppListProvider.Filter.LOCKED && !locked) ||
                    (filter == LockAppListProvider.Filter.UNLOCKED && locked)){
                for (int i = 0; i < 2; i++) {
                    apps.get(i).remove(basicLockInfo);
                    tmpApps[i].remove(basicLockInfo);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void setViewProvider(ViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    @Override
    public android.widget.Filter getFilter() {
        return new SearchFilter();
    }

    public void setFilter(LockAppListProvider.Filter filter) {
        this.filter = filter;
        if (filter == LockAppListProvider.Filter.ALL) {
            tmpApps[0] = new ArrayList<>(advancedApptotAppinfos);
            tmpApps[1] = new ArrayList<>(advancedSwitchtotAppinfos);
            tmpApps[2] = new ArrayList<>(totAppInfos);
        } else {
            tmpApps[0] = getAppInfos(advancedApptotAppinfos, filter == LockAppListProvider.Filter.LOCKED);
            tmpApps[1] = getAppInfos(advancedSwitchtotAppinfos, filter == LockAppListProvider.Filter.LOCKED);
            tmpApps[2] = getAppInfos(totAppInfos, filter == LockAppListProvider.Filter.LOCKED);
        }
        initSearchApps();
    }

    private void initSearchApps() {
        for (int i = 0; i < 3; i++) {
            apps.get(i).clear();
            apps.get(i).addAll(getSortedApps(tmpApps[i], constraint));
        }
        notifyDataSetChanged();
    }

    public void setSortOrder(LockAppListProvider.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        initSearchApps();
    }

    public LockAppListProvider.SortOrder getSortOrder() {
        return sortOrder;
    }

    class SearchFilter extends android.widget.Filter {
        @Override
        protected FilterResults performFiltering(CharSequence searchSeq) {
            List<BasicLockInfo>[] basicLockInfos = new List[3];
            for (int i = 0; i < 3; i++) {
                List<BasicLockInfo> basicLockInfosList = new ArrayList(tmpApps[i]);
                Collections.sort(basicLockInfosList, lockComparator);
                basicLockInfos[i] = getSortedApps(basicLockInfosList, constraint);
            }
            FilterResults filterResults = new FilterResults();
            filterResults.count = basicLockInfos.length;
            filterResults.values = basicLockInfos;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            AppLockSearchableApaterImpl.this.constraint = constraint.toString().toLowerCase();
            Object[] objArray = (Object[]) results.values;
            for (int i = 0; i < results.count; i++) {
                apps.get(i).clear();
                apps.get(i).addAll((List<BasicLockInfo>) objArray[i]);
            }
            AppLockSearchableApaterImpl.this.notifyDataSetChanged();
        }

    }

    private List<BasicLockInfo> getSortedApps(List<BasicLockInfo> appInfos, String query) {
        List<BasicLockInfo> lockAppInfos = new ArrayList<>(appInfos);
        List<BasicLockInfo> sortedApps = new ArrayList<>();
        if (!TextUtils.isEmpty(query)) {
            for (BasicLockInfo lockAppInfo : lockAppInfos) {
                if (lockAppInfo.getLabel().toLowerCase().startsWith(query)) {
                    sortedApps.add(lockAppInfo);
                }
            }
            lockAppInfos.removeAll(sortedApps);
        }
        sortedApps.addAll(lockAppInfos);
        return sortedApps;
    }
}