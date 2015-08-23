package com.smartminds.lockit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.smartminds.lockit.others.NonSwipeableViewPager;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LockRegistrationActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.header_title_txtview)
    TextView headerTitleTxtview;
    @InjectView(R.id.view_pager)
    NonSwipeableViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_registration);
        ButterKnife.inject(this);
    }

    class RegLockAdapter extends FragmentPagerAdapter {

        Fragment[] fragments = new Fragment[]{RegLockFragment.newInstance(), ConfrmLockFragment.newInstance(),
                ConfrmMailFragment.newInstance()};

        public RegLockAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

}
