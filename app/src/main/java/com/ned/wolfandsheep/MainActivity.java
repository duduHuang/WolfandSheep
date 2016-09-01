package com.ned.wolfandsheep;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

/**
 * Created by NedHuang on 2016/8/26.
 */
public class MainActivity extends AppCompatActivity {
    private MainActivity instance = this;
    private FragmentLogin mFragmentLogin = new FragmentLogin();
    private FragmentManager mFM = this.getFragmentManager();
    private FragmentGameCenter mFragmentGameCenter = new FragmentGameCenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitylayout);
        mFM = instance.getFragmentManager();
        mFM.beginTransaction().replace(R.id.fragment_used, mFragmentLogin, FragmentLogin.TAG).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFragmentLogin.setOnConnectionListener(new FragmentLogin.OnLogInListener() {
            @Override
            public void setName(String name) {
                FragmentGameCenter f = new FragmentGameCenter();
                f.init(name);
                mFM.beginTransaction().replace(R.id.fragment_used, f, FragmentGameCenter.TAG).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            }
        });
        mFragmentGameCenter.setOnComeInRoomListener(new FragmentGameCenter.OnGameCenterListener() {
            @Override
            public void onGameRoom(String name) {
                FragmentGameRoom f = new FragmentGameRoom();
                mFM.beginTransaction().replace(R.id.fragment_used, f, FragmentGameRoom.TAG).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
