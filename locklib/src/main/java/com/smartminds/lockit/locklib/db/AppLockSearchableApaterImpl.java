package com.smartminds.lockit.locklib.db;

import android.util.Log;
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
import com.smartminds.lockit.locklib.common.lockscreen.Filters;
import com.smartminds.lockit.locklib.common.lockscreen.ViewProvider;
import com.smartminds.lockit.locklib.others.MultiHeaderAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private Filters.Filter filter;
    private Filters.SortOrder sortOrder;
    private LockAppListProviderImpl.LockComparator lockComparator;


    AppLockSearchableApaterImpl(UserProfile userProfile,
                                Filters.Filter filter, Filters.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        apps.put(0, new CopyOnWriteArrayList<BasicLockInfo>());
        apps.put(1, new CopyOnWriteArrayList<BasicLockInfo>());
        apps.put(2, new CopyOnWriteArrayList<BasicLockInfo>());
        lockComparator = new LockAppListProviderImpl.LockComparator(sortOrder);
        appListProvider = AppLockLib.getInstance().getAppListProvider();
        setUserProfile(userProfile, filter);
        setFilter(filter);
        initSearchApps();
    }

    public void setUserProfile(UserProfile userProfile, Filters.Filter filter) {
        this.userProfile = userProfile;
        this.filter = filter;
        AdvancedAppLock advancedLock = appListProvider.getAdvancedLock(userProfile);
        BasicLockInfo[] appListInfo = appListProvider.getAppListInfo(userProfile, filter, sortOrder);
        totAppInfos = new ArrayList<>(Arrays.asList(appListInfo));
//        totAppInfos.remove(advancedLock.getAdvancedLock().getBasicLockInfo(
//                AdvancedAppLock.AdvancedLocks.Type.SETTINGS));
//        totAppInfos.remove(advancedLock.getAdvancedLock().getBasicLockInfo(
//                AdvancedAppLock.AdvancedLocks.Type.PLAYSTORE));
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
        viewProvider.fillChildView(convertView, basicLockInfo,constraint);
    }

    @Override
    public View getNormalView(BasicLockInfo basicLockInfo, View convertView, ViewGroup parent) {
        return viewProvider.getChildView();
    }

    @Override
    public void lockApp(BasicLockInfo basicLockInfo, boolean locked) {
        if (basicLockInfo instanceof LockAppInfoImpl) {
            String pkgName = ((LockAppInfo) basicLockInfo).getPackageName();
            List<BasicLockInfo> allApps = tmpApps[2];
            int appCounts = 0;
            for (int i = allApps.size()-1; i >= 0; i--) {
                BasicLockInfo basicLockAppInfo = allApps.get(i);
                if (pkgName.equals(((LockAppInfo) basicLockAppInfo).getPackageName())) {
                    appCounts++;
                    appListProvider.lockApp(basicLockAppInfo, locked, userProfile);
                    basicLockAppInfo.setLock(locked);
                    if ((filter == Filters.Filter.LOCKED && !locked) ||
                            (filter == Filters.Filter.UNLOCKED && locked)) {
                        apps.get(2).remove(basicLockAppInfo);
                        tmpApps[2].remove(basicLockAppInfo);
                    }
                }
            }
            if ((filter == Filters.Filter.ALL && appCounts > 1) || filter != Filters.Filter.ALL)
                notifyDataSetChanged();
        } else {
            int index = basicLockInfo instanceof AdvancedAppLock.AdvancedLocks.AdvancedAppBasicLock ? 1 : 2;
            if ((filter == Filters.Filter.LOCKED && !locked) ||
                    (filter == Filters.Filter.UNLOCKED && locked)) {
                apps.get(index).remove(basicLockInfo);
                tmpApps[index].remove(basicLockInfo);
            }
            basicLockInfo.setLock(locked);
            appListProvider.lockApp(basicLockInfo, locked, userProfile);
        }
    }

    @Override
    public void setViewProvider(ViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    @Override
    public void setSortOrder(Filters.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        initSearchApps();
    }

    @Override
    public Filters.SortOrder getSortOrder() {
        return sortOrder;
    }

    @Override
    public android.widget.Filter getFilter() {
        return new SearchFilter();
    }

    public void setFilter(Filters.Filter filter) {
        this.filter = filter;
        if (filter == Filters.Filter.ALL) {
            tmpApps[0] = new CopyOnWriteArrayList<>(advancedApptotAppinfos);
            tmpApps[1] = new CopyOnWriteArrayList<>(advancedSwitchtotAppinfos);
            tmpApps[2] = new CopyOnWriteArrayList<>(totAppInfos);
        } else {
            tmpApps[0] = getAppInfos(advancedApptotAppinfos, filter == Filters.Filter.LOCKED);
            tmpApps[1] = getAppInfos(advancedSwitchtotAppinfos, filter == Filters.Filter.LOCKED);
            tmpApps[2] = getAppInfos(totAppInfos, filter == Filters.Filter.LOCKED);
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

    @Override
    public Filters.Filter getAppLockFilter() {
        return filter;
    }

    @Override
    public void setAppLockFilter(Filters.Filter filter) {
        setFilter(filter);
    }

    class SearchFilter extends android.widget.Filter {
        @Override
        protected FilterResults performFiltering(CharSequence searchSeq) {
            List<BasicLockInfo>[] basicLockInfos = new List[3];
            for (int i = 0; i < 3; i++) {
                List<BasicLockInfo> basicLockInfosList = new ArrayList(tmpApps[i]);
                Collections.sort(basicLockInfosList, lockComparator);
                basicLockInfos[i] = getSortedApps(basicLockInfosList, searchSeq.toString().toLowerCase());
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
        for (BasicLockInfo lockAppInfo : lockAppInfos) {
            if (lockAppInfo.getLabel().toLowerCase().startsWith(query) ||
                    lockAppInfo.getLabel().toLowerCase().contains(query)) {
                sortedApps.add(lockAppInfo);
            }
        }
        return sortedApps;
    }
}