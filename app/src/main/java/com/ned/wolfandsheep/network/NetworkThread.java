package com.ned.wolfandsheep.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ned.wolfandsheep.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Created by NedHuang on 2016/8/26.
 */
public class NetworkThread extends Thread {
    private final static String TAG = "NetworkThread";
    private Activity mAct = null;
    private String mName ="", mRoomName = "";
    private final static int sServerPort = 15300;
    private final static String sServerIP = "118.163.85.223";
    private DatagramSocket mSocket = null;
    private boolean bReg = false;
    private onConnectionListener mOnConnectionListener = null;
    private int mCmd;
    private int mUserID;

    private static final int sGroupCode = 0xAD;
    private static final int sReserved = (4 * 2);
    private static final int sPayloadReserved = 50;

    public interface onConnectionListener {
        void setReg(boolean isReg, int id);
    }

    public void setOnConnectionListener(onConnectionListener connectionListener) {
        mOnConnectionListener = connectionListener;
    }

    public NetworkThread(Activity a, String name, int cmd) {
        mAct = a;
        mName = name;
        mCmd = cmd;
    }

    public void setRoomName(String roomName) {
        mRoomName = roomName;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                break;
            }
            if (isNetworkAlive()) {
                if (CheckServer.checkServer(sServerIP, sServerPort)) {
                    try {
                        mSocket = new DatagramSocket(sServerPort);
                        while (!bReg && !Thread.interrupted()) {
//                            testCode();
                            transmitCmd(mName, mCmd);
                            ackCmd();
                        }
                        mSocket.close();
                        mSocket = null;
                        break;
                    } catch (SocketException e) {
                        Log.d(TAG, "run: " + e.getMessage());
                    }
                }
            }
        }
        // interrupt is true.
        mOnConnectionListener.setReg(bReg, mUserID);
        Log.d(TAG, "Connect successful.");
    }
    private static class CheckServer {

        private static boolean bServerExist = false;
        private static DatagramSocket sGramSocket = null;

        private static boolean checkServer(final String ip, final int port) {
            Thread a = new Thread(new Runnable() {
                @Override
                public void run() {
                    bServerExist = false;
                    SocketAddress address = new InetSocketAddress(ip, port);
                    try {
                        sGramSocket = new DatagramSocket();
                        sGramSocket.connect(address);
                        bServerExist = true;
                    } catch (IOException e) {
                        bServerExist = false;
                        Log.d(TAG, "Connection fail.\n" + e.getMessage());
                    }
                }
            });
            a.start();
            try {
                a.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (sGramSocket != null) {
                sGramSocket.close();
                sGramSocket = null;
            }
            return bServerExist;
        }
    }

    private boolean isNetworkAlive() {
        if (mAct != null) {
            ConnectivityManager cm = (ConnectivityManager) mAct.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                return true;
            } else {
                return false;
            }
        } else {
            Log.d(TAG, "isNetworkAlive: mAct is null.");
            return false;
        }
    }

    private void transmitCmd(String name, int cmd) {
        try {
            SocketAddress address = new InetSocketAddress(sServerIP, sServerPort);
            byte[] bytes;
            char[] chars = name.toCharArray();
            DatagramPacket clientPacket;
            if (cmd == R.string.login) {
                bytes = new byte[(4 * 3) + sReserved + 1 + 32 + sPayloadReserved];
                bytes[0] = (byte) sGroupCode;
                bytes[1] = (byte) 0x01;
                bytes[2] = (byte) 0x01;
                bytes[3] = (byte) 0x01;
                for (int i = 4; i < 12; i++) {
                    bytes[i] = 0;
                }
                for (int i = 12; i < (12 + sReserved); i++) {
                    bytes[i] = 0;
                }
                bytes[12 + sReserved] = 0x02;
                byte[] b = chars2Bytes(chars);
                System.arraycopy(b, 0, bytes, (12 + sReserved + 1), b.length);
                for (int i = (12 + sReserved + 1 + b.length); i < (12 + sReserved + 1 + 32 + sPayloadReserved); i++) {
                    bytes[i] = 0;
                }
                clientPacket = new DatagramPacket(bytes, bytes.length, address);
                mSocket.send(clientPacket);
            } else if (cmd == R.string.create) {
                bytes = new byte[(4 * 3) + sReserved + 32];
                bytes[0] = (byte) sGroupCode;
                bytes[1] = (byte) 0x01;
                bytes[2] = (byte) 0x01;
                bytes[3] = (byte) 0x01;
                for (int i = 4; i < 12; i++) {
                    bytes[i] = 0;
                }
                for (int i = 12; i < (12 + sReserved); i++) {
                    bytes[i] = 0;
                }
                byte[] b = chars2Bytes(chars);
                System.arraycopy(b, 0, bytes, (12 + sReserved), b.length);
                clientPacket = new DatagramPacket(bytes, bytes.length, address);
                mSocket.send(clientPacket);
            } else if (cmd == R.string.join) {

            }
        } catch (Exception e) {
            Log.d(TAG, "transmitCmd: " + e.getMessage());
        }
    }

    private void ackCmd() {
        byte[] bytes = new byte[(4 * 3) + sReserved + 1 + 4 + 4 + sPayloadReserved];
        try {
            DatagramPacket clientPacket = new DatagramPacket(bytes, bytes.length);
            Log.d(TAG, "ackCmd: Receive...");
            mSocket.setSoTimeout(3000);
            mSocket.receive(clientPacket);
            Log.d(TAG, "recLogInCmd: GroupCode: " + Integer.toHexString(bytes[0]));
            Log.d(TAG, "recLogInCmd: CmdID: " + bytes[1]);
            Log.d(TAG, "recLogInCmd: SendTo: " + bytes[2]);
            Log.d(TAG, "recLogInCmd: Version: " + bytes[3]);
            for (int i = 4; i < 8; i++) {
                Log.d(TAG, "recLogInCmd: ID: " + bytes[i]);
            }
            for (int i = 8; i < 12; i++) {
                Log.d(TAG, "recLogInCmd: ID CheckSum: " + bytes[i]);
            }
            for (int i = 12; i < (12 + sReserved); i++) {
                Log.d(TAG, "recLogInCmd: Reserved: " + bytes[i]);
            }
            Log.d(TAG, "recLogInCmd: Status: " + bytes[(4 * 3) + sReserved]);
            for (int i = ((4 * 3) + sReserved + 1); i < ((4 * 3) + sReserved + 1 + 4); i++) {
                Log.d(TAG, "recLogInCmd: ID: " + bytes[i]);
            }
            mUserID = byte2int(bytes[(4 * 3) + sReserved + 1], bytes[(4 * 3) + sReserved + 1 + 1], bytes[(4 * 3) + sReserved + 1 + 2], bytes[(4 * 3) + sReserved + 1 + 3]);
            Log.d(TAG, "recLogInCmd: ID: " + mUserID);
            for (int i = ((4 * 3) + sReserved + 1 + 4); i < ((4 * 3) + sReserved + 1 + 4 + 4); i++) {
                Log.d(TAG, "recLogInCmd: ID checksum: " + bytes[i]);
            }
            int idCheckSum = byte2int(bytes[(4 * 3) + sReserved + 1 + 4], bytes[(4 * 3) + sReserved + 1 + 4 + 1], bytes[(4 * 3) + sReserved + 1 + 4 + 2], bytes[(4 * 3) + sReserved + 1 + 4 + 3]);
            Log.d(TAG, "recLogInCmd: ID checksum: " + idCheckSum);
            for (int i = ((4 * 3) + sReserved + 1 + 4 + 4); i < bytes.length; i++) {
                Log.d(TAG, "recLogInCmd: sPayloadReserved: " + bytes[i]);
            }
            bReg = true;
        } catch (IOException e) {
            Log.d(TAG, "ackCmd: " + e.getMessage());
            bReg = false;
        }
    }

    private byte[] chars2Bytes(char[] chars) {
        Charset cs = Charset.forName ("UTF-8");
        CharBuffer cb = CharBuffer.allocate (chars.length);
        cb.put (chars);
        cb.flip ();
        ByteBuffer bb = cs.encode (cb);
        return bb.array();
    }

    private int byte2int(byte b0, byte b1, byte b2, byte b3) {
        return Integer.parseInt(String.valueOf((Integer.parseInt(Integer.toHexString(b3)) * 1000000) +
                (Integer.parseInt(Integer.toHexString(b2)) * 10000) +
                (Integer.parseInt(Integer.toHexString(b1)) * 100) +
                (Integer.parseInt(Integer.toHexString(b0)) & 0xff)), 16);
    }

    private void testCode() {
        byte[] msg = {(byte) 0xad, 01, 01, 01, 12, 34, 56, 78, 12, 34, 56, 78, 12, 34, 56, 78, 12, 34, 56,78 };
        try {
            DatagramSocket socket = new DatagramSocket(sServerPort);
            SocketAddress address = new InetSocketAddress(sServerIP, sServerPort);
            DatagramPacket clientPacket = new DatagramPacket(msg, msg.length, address);
            socket.send(clientPacket);
            byte[] bytes = new byte[1024];
            DatagramPacket serverPacket = new DatagramPacket(bytes, msg.length + 9);
            socket.receive(serverPacket);
            for (int i = 0; i < serverPacket.getLength(); i++)
                Log.d(TAG, "testCode: " + Integer.toHexString(bytes[i]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
