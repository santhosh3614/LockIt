package com.smartminds.lockit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartminds.lockit.locklib.common.lock.LockData;
import com.smartminds.lockit.locklib.common.lock.LockScreen;
import com.smartminds.lockit.locklib.common.lock.LockScreen.CreatePassCallBack;
import com.smartminds.lockit.others.BaseFragment;

/**
 * Created by santhosh on 8/7/15.
 */
public class RegLockFragment extends BaseFragment implements CreatePassCallBack {


    public static RegLockFragment newInstance(){
        return new RegLockFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onCreatePassCancel() {
        
    }

    @Override
    public void onCreatePassValid(LockData data) {

    }

    @Override
    public void onCreatePassInvalid(LockData data) {

    }
}
