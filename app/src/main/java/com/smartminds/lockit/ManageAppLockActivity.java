package com.smartminds.lockit;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.smartminds.lockit.common.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.smartminds.lockit.ManageLockFragment.ACTION_CREATE_APPLOCKS;
import static com.smartminds.lockit.ManageLockFragment.EXTRA_ACTION_TYPE;

/**
 * Created by santhosh on 5/7/15.
 */
public class ManageAppLockActivity extends BaseActivity {

    public static final String MANAGE_NEW_PROFILE = "manage_new_profile";
    public static final String EXTRA_PROFILE = "profile";
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.container)
    FrameLayout container;
    private ManageLockFragment manageLockFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manageapplock);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (savedInstanceState == null) {
            manageLockFragment = ManageLockFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_ACTION_TYPE, ACTION_CREATE_APPLOCKS);
            manageLockFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, manageLockFragment).commit();
        } else {
            manageLockFragment = (ManageLockFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        }
    }

    @Override
    public void onBackPressed() {
        manageLockFragment.onBackPressed();
        super.onBackPressed();
    }
}
