package com.socketPackage;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Administrator on 2016/7/11.
 */
public class NetClient extends Thread {

    private Handler _outHandler;    //snd msg to other thread through this
    private Handler _curHandler;    //rcv msg from other thread with this
    private Socket _sockClient;
    private String _strIp;
    private int _nPort;
    private OutputStream _os;
    private InputStream  _is;

    private static final String STR_STATE_KEY = "state";
    private static final String STR_PATN_CMD = "cmd";
    private static final int INT_PATN_CMD = 1;
    private static final int INT_PATN_FILE = 2;
    private static final int SIZE_RCV_BUF=1000;

    public int initSocket(String Ip, int nPort) throws IOException {
        _strIp = Ip;
        _nPort = nPort;
        return 1;
    }

    /**
     * put msg in que and send in sub thread. the pub interface for send.
     * @param strCmd  the str contend to send
     * @return        the len of send str
     */
    public int putMsgInQue(String strCmd){
        Log.i("----111","通过");
        Message msg=_curHandler.obtainMessage();
        Bundle b= new Bundle();                     //form the msg frame.
        b.putInt(STR_STATE_KEY, INT_PATN_CMD);
        b.putString(STR_PATN_CMD,strCmd);
        Log.i("----222","通过");
        msg.setData(b);
        _curHandler.sendMessage(msg);
        Log.i("----333","通过");
        return strCmd.length();
    }

    @Override
    public void run() {
        Looper.prepare();
        try {
            _sockClient=new Socket(_strIp,_nPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        _curHandler = new Handler() {
            public void handleMessage(Message msg) {

                switch (msg.getData().getInt(STR_STATE_KEY)){

                    case INT_PATN_CMD: //send cmd to tcp server
                        try {
                            sendData(msg.getData().getString(STR_PATN_CMD));
                            System.out.println(rcvData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    case INT_PATN_FILE:
                        ;
                }
            }
        };

        Looper.loop();
    }

    /**
     * @param strSnd the str to send
     * @return the length of send str
     * @throws IOException
     */
    private int sendData(String strSnd) throws IOException, NullPointerException {
        _os = _sockClient.getOutputStream();
        _os.write(strSnd.getBytes());
        return strSnd.length();
    }

    /**
     *  get the tcp data rcv.  encode the bytes to gbk str.
     * @return  gbk str from tcp rcv bytes
     * @throws IOException
     */
    private String rcvData() throws IOException {
        _is=_sockClient.getInputStream();
        byte[] rcvBuf=new byte[SIZE_RCV_BUF];
        _is.read(rcvBuf);
        return new String(rcvBuf,"gbk");
    }

}