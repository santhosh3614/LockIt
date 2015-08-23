package com.smartminds.lockit;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class NavigationDrawerFragment extends Fragment {

    @InjectView(R.id.list_view)
    ListView listView;
    private String[] items;
    private int selectedIdx;
    private NavigationDrawerOnItemClick navigationListener;
    private NavAdapter navAdapter;

    public static NavigationDrawerFragment getInstance() {
        return new NavigationDrawerFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        navigationListener = (NavigationDrawerOnItemClick) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new String[]{getString(R.string.applock), getString(R.string.photovault),
                getString(R.string.videovault), getString(R.string.settings_label)};
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
        selectedIdx = position;
        position--;
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
        navAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigationListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int height = getResources().getDisplayMetrics().heightPixels;
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.navgaton_drawer_header, null);
        headerView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * .3f)));
        listView.addHeaderView(headerView);
        navAdapter = new NavAdapter(items);
        listView.setAdapter(navAdapter);
    }

    class NavAdapter extends BaseAdapter {
        private String[] list;

        public NavAdapter(String[] list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public String getItem(int position) {
            return list[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.drawer_list_item, null);
            }
            convertView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            View selView = convertView.findViewById(R.id.left_select_item);
//            if (selectedIdx == position) {
                selView.setBackgroundResource(R.color.colorAccent);
//            } else {
//                selView.setBackgroundResource(android.R.color.transparent);
//            }
            ((TextView) convertView.findViewById(R.id.name_txtview)).setText(getItem(position));
            return convertView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    interface NavigationDrawerOnItemClick {
        void onAppLockItemClick();

        void onPhotoVaultClick();

        void onVideoVaultClick();

        void onAppSettingsClick();
    }
}
