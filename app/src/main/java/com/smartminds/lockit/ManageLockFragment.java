package com.smartminds.lockit;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.LockAppListProvider;
import com.smartminds.lockit.locklib.UserProfile;
import com.smartminds.lockit.locklib.UserProfileProvider;
import com.smartminds.lockit.locklib.common.lockscreen.AppLockSearchableAdapter;
import com.smartminds.lockit.locklib.common.lockscreen.Filters;
import com.smartminds.lockit.others.BaseFragment;
import com.smartminds.lockit.others.Utils;
import com.smartminds.lockit.settings.UserProfileActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.appsforbb.common.appbase.AppBase.getAppContext;

public class ManageLockFragment extends BaseFragment implements AdapterView.OnItemSelectedListener, View.OnFocusChangeListener {

    @InjectView(R.id.empty_txtview)
    TextView emptyTxtview;
    @InjectView(R.id.lock_apps_listview)
    ListView lockAppsListview;
    @InjectView(R.id.profile_btn)
    FloatingActionButton profileBtn;
    public static String ACTION_CREATE_APPLOCKS = "NEW_APPLOCKS";
    public static String EXTRA_ACTION_TYPE = "action_type";
    @InjectView(R.id.cancel_button)
    Button cancelButton;
    @InjectView(R.id.save_button)
    Button saveButton;
    @InjectView(R.id.footer)
    LinearLayout footer;

    private UserProfile userProfile;
    private UserProfileProvider userProfileProvider;
    private LockAppListProvider appListProvider;
    private AppLockSearchableAdapter adapter;
    private LockAppViewProvider lockViewProvider;
    private SwitchCompat enableSwitch;
    private SearchView searchView;
    private Spinner spinner;
    private Filters.Filter filter = Filters.Filter.ALL;
    private View dummyView;
    private String action;
    private boolean isNewProfile;

    public static ManageLockFragment newInstance() {
        ManageLockFragment fragment = new ManageLockFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLockLib appLockLib = AppLockLib.getInstance();
        userProfileProvider = appLockLib.getUserProfileProvider();
        appListProvider = appLockLib.getAppListProvider();
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            action = getArguments().getString(EXTRA_ACTION_TYPE);
        }
        if (action != null && action.equals(ACTION_CREATE_APPLOCKS)) {
            isNewProfile = true;
            userProfile = userProfileProvider.addProfile("dummy");
            getActivity().setTitle(getString(R.string.add_new_profile));
        } else {
            userProfile = userProfileProvider.getProfile();
            getActivity().setTitle(getString(R.string.applock));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isNewProfile) {
                userProfileProvider.removeProfile(userProfile);
            }
            getActivity().setResult(isNewProfile ? Activity.RESULT_CANCELED : Activity.RESULT_OK);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        if (isNewProfile) {
            userProfileProvider.removeProfile(userProfile);
        }
        getActivity().setResult(isNewProfile ? Activity.RESULT_CANCELED : Activity.RESULT_OK);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_lockapp, menu);
        MenuItem spinnerMenu = menu.findItem(R.id.action_spinner);
        spinner = (Spinner) spinnerMenu.getActionView();
        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                android.R.id.text1, new String[]{"All", "Lock", "UnLock"}));
        spinner.setOnItemSelectedListener(this);
        MenuItem searchMenu = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenu.getActionView();
        searchView.setOnQueryTextFocusChangeListener(this);
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
        MenuItem switchMenu = menu.findItem(R.id.myswitch);
        enableSwitch = (SwitchCompat) switchMenu.getActionView();
        enableSwitch.setChecked(AppLockLib.getInstance().isEnabled());
        enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppLockLib.getInstance().setEnabled(isChecked);
            }
        });
        if (isNewProfile) {
            switchMenu.setVisible(false);
            spinnerMenu.setVisible(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lock, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        dummyView = new View(getActivity());
        int height = (int) Utils.convertDpToPixel(50, getAppContext());
        dummyView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height));
        if (isNewProfile) {
            footer.setVisibility(View.VISIBLE);
            profileBtn.setVisibility(View.INVISIBLE);
        } else {
            lockAppsListview.addFooterView(dummyView);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = appListProvider.getAdapter(filter,
                Filters.SortOrder.NAME, userProfile);
        lockViewProvider = new LockAppViewProvider(getActivity(), adapter, userProfile);
        adapter.setViewProvider(lockViewProvider);
        lockAppsListview.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        filter = Filters.Filter.values()[position];
        adapter.setAppLockFilter(filter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    @OnClick({R.id.profile_btn, R.id.cancel_button, R.id.save_button})
    public void onClick(View v) {
        if (v.getId() == R.id.profile_btn) {
            startActivity(new Intent(getActivity(), UserProfileActivity.class));
        } else if (v.getId() == R.id.cancel_button) {
            if (isNewProfile) {
                userProfileProvider.removeProfile(userProfile);
            }
            getActivity().setResult(Activity.RESULT_CANCELED);
            getActivity().finish();
        } else if (v.getId() == R.id.save_button) {
            getActivity().setResult(Activity.RESULT_OK);
            pushDialog();
        }
    }

    private void pushDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getActivity());
        builder.setTitle(getString(R.string.enter_profile_name)).setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editText.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    AppLockLib.getInstance().getUserProfileProvider().updateUserProfile(userProfile, name);
                    Intent intent = new Intent();
                    intent.putExtra(UserProfileActivity.EXTRA_PROFILE, userProfile);
                    dialog.cancel();
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.invalid_profile_name), Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isNewProfile) {
                    userProfileProvider.removeProfile(userProfile);
                }
                dialog.cancel();
            }
        }).setView(editText);
        builder.show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!isNewProfile && profileBtn != null) {
            profileBtn.setVisibility(!hasFocus ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
