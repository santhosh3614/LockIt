package com.smartminds.lockit.settings;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.smartminds.lockit.ManageAppLockActivity;
import com.smartminds.lockit.R;
import com.smartminds.lockit.common.BaseActivity;
import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.UserProfile;
import com.smartminds.lockit.locklib.UserProfileProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserProfileActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final int MANAGE_NEW_PROFILE_REQ_CODE = 123;
    public static final String EXTRA_PROFILE = "profile";
    @InjectView(R.id.user_profile_listview)
    ListView listView;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    private UserProfileProvider userProfileProvider;
    private List<UserProfile> userProfiles;
    private ArrayAdapter<UserProfile> userProfileArrayAdapter;
    private UserProfile enabledUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
//        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        userProfileProvider = AppLockLib.getInstance().getUserProfileProvider();
        userProfiles = new ArrayList<>(Arrays.asList(userProfileProvider.getAllUserProfiles()));
        userProfileArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_single_choice, userProfiles);
        listView.setAdapter(userProfileArrayAdapter);
        listView.setItemsCanFocus(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        enabledUserProfile = userProfileProvider.getProfile();
        for (int i = 0; i < userProfiles.size(); i++) {
            if (userProfiles.get(i).equals(enabledUserProfile)) {
                listView.setItemChecked(i, true);
                break;
            }
        }
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
    }

    void onProfileEdit(UserProfile userProfile) {
//        Intent intent = new Intent(this, ManageAppLockActivity.class);
//        intent.setAction(ManageAppLockActivity.MANAGE_EDIT_PROFILE);
//        intent.putExtra(ManageAppLockActivity.EXTRA_PROFILE, (android.os.Parcelable) userProfile);
//        startActivityForResult(intent, 111);
    }

    @Override
    public void onBackPressed() {
        
        super.onBackPressed();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_user_profile, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_profile) {
            Intent intent = new Intent(this, ManageAppLockActivity.class);
            intent.setAction(ManageAppLockActivity.MANAGE_NEW_PROFILE);
            startActivityForResult(intent, MANAGE_NEW_PROFILE_REQ_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        UserProfile userProfile = userProfiles.get((int) info.id);
        switch (item.getItemId()) {
            case R.id.edit:
                onProfileEdit(userProfile);
                return true;
            case R.id.delete:
                if (userProfiles.size() > 1) {
                    userProfileProvider.removeProfile(userProfile);
                    userProfiles.remove(userProfile);
                    userProfileArrayAdapter.notifyDataSetChanged();
                    if (userProfile.equals(enabledUserProfile)) {
                        enabledUserProfile = userProfiles.get(0);
                        userProfileProvider.enableProfile(enabledUserProfile);
                        listView.setItemChecked(0, true);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.min_one_profile_nedded), Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MANAGE_NEW_PROFILE_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                UserProfile userProfile = (UserProfile) data.getParcelableExtra(ManageAppLockActivity.EXTRA_PROFILE);
                userProfiles.add(userProfile);
                userProfileArrayAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        enabledUserProfile = userProfiles.get(position);
        userProfileProvider.enableProfile(enabledUserProfile);
    }
}
