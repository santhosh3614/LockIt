package com.smartminds.lockit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.LockAppListProvider;
import com.smartminds.lockit.locklib.UserProfile;
import com.smartminds.lockit.locklib.UserProfileProvider;
import com.smartminds.lockit.locklib.common.lockscreen.AppLockSearchableAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LockFragment extends Fragment {

    @InjectView(R.id.empty_txtview)
    TextView emptyTxtview;
    @InjectView(R.id.lock_apps_listview)
    ListView lockAppsListview;

    private UserProfile userProfile;
    private UserProfileProvider userProfileProvider;
    private LockAppListProvider appListProvider;
    private AppLockSearchableAdapter adapter;
    private LockAppViewProvider lockViewProvider;
    private SwitchCompat enableSwitch;
    private SearchView searchView;

    public static LockFragment newInstance() {
        LockFragment fragment = new LockFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLockLib appLockLib = AppLockLib.getInstance();
        userProfileProvider = appLockLib.getUserProfileProvider();
        userProfile = userProfileProvider.getProfile();
        appListProvider = appLockLib.getAppListProvider();
        getActivity().setTitle(getString(R.string.applock));
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.lockapp_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null) {
                    adapter.getFilter().filter(query);
                    lockAppsListview.setSelection(0);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                    lockAppsListview.setSelection(0);
                }
                return true;
            }
        });
        enableSwitch = (SwitchCompat) menu.findItem(R.id.myswitch).getActionView();
        enableSwitch.setChecked(AppLockLib.getInstance().isEnabled());
        enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppLockLib.getInstance().setEnabled(isChecked);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lock, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = appListProvider.getAdapter(LockAppListProvider.Filter.ALL,
                LockAppListProvider.SortOrder.NAME, userProfile);
        lockViewProvider = new LockAppViewProvider(getActivity(), adapter, userProfile);
        adapter.setViewProvider(lockViewProvider);
        lockAppsListview.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
