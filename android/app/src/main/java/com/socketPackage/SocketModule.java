package com.socketPackage;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketModule extends ReactContextBaseJavaModule {

    public static final String REACTCLASSNAME = "SocketManager";
    private Context mContext;
    private String result;
    private NetClient mNetClient;
    private Socket socketTop;
    private boolean ifListen  =true;
    private String socketIp;
    private int socketPort;
    private Thread socketThread;
    public SocketModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        GetLogTask.mContext=reactContext;
    }
    @Override
    public String getName() {
        return REACTCLASSNAME;
    }
//    初始化socket及监听
    @ReactMethod
    public void creatSocketManager(String ip, int port,Callback callback) {
        this.socketIp=ip;
        this.socketPort=port;
        try{
            socketTop=MyHungerySocket.getSocket(ip,port);
            if(null!=socketTop){
                GetLogTask task = new GetLogTask(socketTop,socketIp,socketPort);
                task.execute();
                initTcpListener();
                callback.invoke(true);
            }else{
                callback.invoke(false);
            }
        }catch (IOException e1){
            Log.i("----",e1.toString());
            callback.invoke(false);
        }
    }
//    socket监听
    public void initTcpListener(){
        /**开始监听*/
        socketThread = new Thread() {
            public void run() {
                while (ifListen) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        //判断远端是否断开了连接
                        socketTop.sendUrgentData(0xFF);
                        Log.i("----","---------------------------成功");
                    } catch (IOException e1) {
                        ifListen=false;
                        try {
                            socketTop=MyHungerySocket.getSocket(socketIp,socketPort);
                            GetLogTask task = new GetLogTask(socketTop,socketIp,socketPort);
                            task.execute();
                            ifListen=true;
                            Log.i("----","---------------失败");
                        } catch (IOException e) {
                            Log.i("----","------创建失败");
                            ifListen=true;
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        socketThread.start();
    }
//    发送消息
    @ReactMethod
    public void sendMessage(String message) {
        Handler handler=new Handler();
        new SendThread(getCurrentActivity(),handler,socketTop,socketIp,socketPort,message).start();
    }
//    接收消息
//    @ReactMethod
//    public void regeistMsg() {
//        GetLogTask task = new GetLogTask(socketTop,socketIp,socketPort);
//        task.execute();
//    }
//    向RN广播消息
    public static void sendEvent(ReactContext reactContext, String eventName, @Nullable String paramss)
    {
        System.out.println("reactContext="+reactContext);
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, paramss);

    }
//    @ReactMethod
//    public void startPlan1(String ip,int port) {
//        mNetClient=new NetClient();
//        try {
//            mNetClient.initSocket(ip,port);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    @ReactMethod
//    public void sendPlan1(String aa){
//        mNetClient.putMsgInQue(aa);
//    }

}