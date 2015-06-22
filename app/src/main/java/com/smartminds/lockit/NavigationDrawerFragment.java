package com.smartminds.lockit;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class NavigationDrawerFragment extends Fragment {

    @InjectView(R.id.list_view)
    ListView listView;
    private String[] items;
    private NavigationDrawerOnItemClick navigationListener;

    public static NavigationDrawerFragment getInstance(){
        return new NavigationDrawerFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        navigationListener=(NavigationDrawerOnItemClick)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new String[]{getString(R.string.applock), getString(R.string.photovault),
                getString(R.string.videovault), getString(R.string.settings)};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @OnItemClick(R.id.list_view)
    void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                navigationListener.onAppLockItemClick();
                break;
            case 1:
                navigationListener.onPhotoVaultClick();
                break;
            case 2:
                navigationListener.onVideoVaultClick();
                break;
            case 3:
                navigationListener.onAppSettingsClick();
                break;
            default:
                return;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigationListener=null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(),
                R.layout.drawer_list_item, R.id.name_txtview, items));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    interface NavigationDrawerOnItemClick{
        void onAppLockItemClick();
        void onPhotoVaultClick();
        void onVideoVaultClick();
        void onAppSettingsClick();
    }
}
