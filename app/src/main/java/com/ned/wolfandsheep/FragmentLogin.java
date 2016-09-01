package com.ned.wolfandsheep;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ned.wolfandsheep.network.NetworkThread;

/**
 * Created by NedHuang on 2016/8/26.
 */
public class FragmentLogin extends Fragment {
    public final static String TAG = "FragmentLogIn";
    private EditText editTxt_para1, editTxt_para2;
    private Button btn_Login, btn_Exit;
    private TextView txt_Libraries;
    ViewGroup vg;
    View vf;
    private FragmentLogin instance = this;
    private FragmentManager mFM;
    private boolean bReg = false;
    private OnLogInListener mOnLogInListener = null;

    // NetworkThread part
    private NetworkThread mNetworkThread = null;

    public interface OnLogInListener {
        void setName(String name);
    }

    public void setOnConnectionListener(OnLogInListener logInListener) {
        mOnLogInListener = logInListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        vg = container;
        mFM = instance.getFragmentManager();
        vf = inflater.inflate(R.layout.loginlayout, container, false);
        initButtonAndSurfaceView();
        btn_Login.setOnClickListener(mOnClickListener);
        btn_Exit.setOnClickListener(mOnClickListener);
        txt_Libraries.setText(Html.fromHtml("<u>" + getResources().getString(R.string.open_source) + "</u>"));
        txt_Libraries.setOnClickListener(mOnClickListener);
        return vf;
    }

    private void initButtonAndSurfaceView() {
        editTxt_para1 = (EditText) vf.findViewById(R.id.editUsername);
        editTxt_para2 = (EditText) vf.findViewById(R.id.editPassword);
        editTxt_para1.setText("qqqq");
        editTxt_para2.setText("qqqq");
        btn_Login = (Button) vf.findViewById(R.id.btnSave);
        btn_Exit = (Button) vf.findViewById(R.id.btnLogout);
        txt_Libraries = (TextView) vf.findViewById(R.id.txtLibrary);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnLogout) {
                instance.getActivity().finish();
            } else if (v.getId() == R.id.btnSave) {
                final ProgressDialog progressBar = new ProgressDialog(instance.getActivity());
                progressBar.setTitle(R.string.connect_title);
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.show();
                progressBar.setOnKeyListener(mOnKeyListener);
                if (mNetworkThread != null) {
                    mNetworkThread.interrupt();
                    try {
                        mNetworkThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mNetworkThread = null;
                }
                mNetworkThread = new NetworkThread(instance.getActivity(), editTxt_para1.getText().toString(), R.string.login);
                mNetworkThread.start();
                mNetworkThread.setOnConnectionListener(new NetworkThread.onConnectionListener() {
                    @Override
                    public void setReg(boolean isReg, int id) {
                        progressBar.dismiss();
                        if (isReg) {
                            mOnLogInListener.setName(editTxt_para1.getText().toString());
                        }
                    }
                });
            } else if (v.getId() == R.id.txtLibrary) {

            }
        }

        private DialogInterface.OnKeyListener mOnKeyListener = new DialogInterface.OnKeyListener() {
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
        };
    };
}
