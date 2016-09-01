package com.ned.wolfandsheep;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by NedHuang on 2016/8/30.
 */
public class FragmentGameRoom extends Fragment {

    public final static String TAG = "FragmentGameRoom";
    private FragmentGameRoom instance = this;
    ViewGroup vg;
    View vf;
    private FragmentManager mFM;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
