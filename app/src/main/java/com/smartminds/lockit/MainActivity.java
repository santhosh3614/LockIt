package com.smartminds.lockit;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.smartminds.lockit.common.BaseActivity;
import com.smartminds.lockit.settings.SettingsActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerOnItemClick {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private Fragment drawerFrag;
    private ActionBarDrawerToggle toggle;
    @InjectView(R.id.container)
    FrameLayout containerLayout;
    private static final int REQUEST_CODE_SETTINGS = 143;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(toggle);
        if (savedInstanceState == null) {
            onAppLockItemClick();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            onAppLockItemClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAppLockItemClick() {
        ManageLockFragment manageLockFragment
                = ManageLockFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, manageLockFragment).commit();
    }

    @Override
    public void onPhotoVaultClick() {
        PhotoVaultFragment photoVaultFragment
                = PhotoVaultFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, photoVaultFragment).commit();
    }

    @Override
    public void onVideoVaultClick() {
        VideoVaultFragment videoVaultFragment
                = VideoVaultFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, videoVaultFragment).commit();
    }

    @Override
    public void onAppSettingsClick() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SETTINGS);
    }
}
