package com.socketPackage;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.Socket;

public class GetLogTask extends AsyncTask<Void,Void,String>
{
    public static ReactApplicationContext mContext;
    private Socket s;
    private String socketIp;
    private int socketPort;

    public GetLogTask(Socket s,String ip, int port) {
        this.s=s;
        this.socketIp = ip;
        this.socketPort = port;
    }
    @Override
    protected String doInBackground(Void...param){
        try {
//            Socket s = new Socket(socketIp, socketPort);
            InputStream inputStream = s.getInputStream();
            DataInputStream input = new DataInputStream(inputStream);
            byte[] b = new byte[10240];
            while(true)
            {
                int length = input.read(b);
                String Msg = new String(b, 0, length, "utf-8");
                Log.v("----data",Msg);
                SocketModule.sendEvent(mContext,"tcpData",Msg);
            }

        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return "";
    }
}