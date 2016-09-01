package com.ned.wolfandsheep;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ned.wolfandsheep.network.NetworkThread;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NedHuang on 2016/8/26.
 */
public class FragmentGameCenter extends Fragment {

    public final static String TAG = "FragmentGameCenter";
    private FragmentGameCenter instance = this;
    ViewGroup vg;
    View vf;
    private FragmentManager mFM;
    private NetworkThread mNetworkThread = null;
    private String mName = null;

    private ListView mList;
    private Button mBtnExit, mBtnRenew, mBtnCreate;
    private SimpleAdapter mRoomList;
    private ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
    private OnGameCenterListener mOnGameCenterListener = null;
    private ProgressDialog mProgressBar = null;

    public interface OnGameCenterListener {
        void onGameRoom(String name);
    }

    public void setOnComeInRoomListener(OnGameCenterListener gameCenterListener) {
        mOnGameCenterListener = gameCenterListener;
    }

    public void init(String name) {
        mName = name;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        vg = container;
        mFM = instance.getFragmentManager();
        vf = inflater.inflate(R.layout.gamecenterlayout, container, false);
        initButtonAndSurfaceView();
        mList.setOnItemClickListener(mOnClickRoomListener);
        mBtnExit.setOnClickListener(mOnClickListener);
        mBtnCreate.setOnClickListener(mOnClickListener);
        mBtnRenew.setOnClickListener(mOnClickListener);
        return vf;
    }

    private void initButtonAndSurfaceView() {
        mBtnExit = (Button) vf.findViewById(R.id.btnCenterExit);
        mBtnRenew = (Button) vf.findViewById(R.id.btnCenterRenew);
        mBtnCreate = (Button) vf.findViewById(R.id.btnCreateRoom);
        mList = (ListView) vf.findViewById(R.id.roomlist);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnCenterExit) {
                FragmentLogin f = new FragmentLogin();
                mFM.beginTransaction().replace(R.id.fragment_used, f, FragmentLogin.TAG).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            } else if (v.getId() == R.id.btnCenterRenew) {

            } else if (v.getId() == R.id.btnCreateRoom) {
                createRoom();
            }
        }
    };

    private void createRoom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(instance.getActivity());
        builder.setTitle(R.string.room_name);
        final EditText et = new EditText(instance.getActivity());
        builder.setView(et);
        builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mNetworkThread != null) {
                    mNetworkThread.interrupt();
                    try {
                        mNetworkThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mNetworkThread = null;
                }
                mNetworkThread = new NetworkThread(instance.getActivity(), et.getText().toString(), R.string.create);
                mNetworkThread.start();
                showProgress();
            }
        });
        builder.create().show();
        mNetworkThread.setOnConnectionListener(new NetworkThread.onConnectionListener() {
            @Override
            public void setReg(boolean isReg, int id) {
                mProgressBar.dismiss();
                if (isReg) {
                    mOnGameCenterListener.onGameRoom(et.getText().toString());
                }
            }
        });
    }

    private AdapterView.OnItemClickListener mOnClickRoomListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // join game.
//            joinRoom(name);
        }
    };

    private void joinRoom(String name) {
        showProgress();
        if (mNetworkThread != null) {
            mNetworkThread.interrupt();
            try {
                mNetworkThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mNetworkThread = null;
        }
        mNetworkThread = new NetworkThread(instance.getActivity(), name, R.string.join);
        mNetworkThread.start();
        mNetworkThread.setOnConnectionListener(new NetworkThread.onConnectionListener() {
            @Override
            public void setReg(boolean isReg, int id) {
                mProgressBar.dismiss();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mHandler.post(mRunnable);
//        mRoomList = new SimpleAdapter(instance.getActivity(), items, R.layout.itemlayout, new String[] {"number", "name"}, new int[] {R.id.roomnumber, R.id.roomname});
//        mList.setAdapter(mRoomList);
    }

//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            mRoomList = new SimpleAdapter(instance.getActivity(), items, R.layout.itemlayout, new String[] {"number", "name"}, new int[] {R.id.roomnumber, R.id.roomname});
//            mList.setAdapter(mRoomList);
//        }
//    };

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // do read room number and name.

//            mHandler.sendMessage(mHandler.obtainMessage());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void showProgress() {
        mProgressBar = new ProgressDialog(instance.getActivity());
        mProgressBar.setTitle(R.string.connect_title);
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.show();
        mProgressBar.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mNetworkThread != null) {
                        mNetworkThread.interrupt();
                        try {
                            mNetworkThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mNetworkThread = null;
                    }
                    return true;
                }
                return false;
            }
        });
    }
}
