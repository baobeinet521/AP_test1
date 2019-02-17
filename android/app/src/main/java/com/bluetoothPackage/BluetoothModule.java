package com.bluetoothPackage;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BluetoothModule extends ReactContextBaseJavaModule {


    //蓝牙扫描测试
    private boolean isScanning = false;//是否正在扫描


    private BluetoothSocket mSocket;
    private ArrayList<BluetoothDevice> deviceList;
    private BluetoothAdapter mAdapter;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private Context mContext;

    public BluetoothModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }
    @Override
    public String getName() {
        return "BluetoothManager1";
    }

    //自制
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device;
            switch (action) {
                case BluetoothDevice.ACTION_FOUND://发现蓝牙
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    deviceList.add(device);
                    String str = device.getName();//获取搜索到的蓝牙的名称
                    Log.i("====",str);
                    Log.i("====",device.toString());
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    //这里可以做提示 搜索完成
                    break;
            }
        }
    };
    public void connetServer(BluetoothDevice mSerDevice){
        if(mSerDevice!=null){
            String SPP_UUID ="10001105-0000-1000-8000-00805f9b34fb";//准备一个UUID码
            //10001101-0000-1000-8000-00805f9b34fb

            UUID uuid = UUID.fromString(SPP_UUID);
            try {
                mSocket = mSerDevice.createRfcommSocketToServiceRecord(uuid);
                mSocket.connect();//在连接上之前会一种阻塞
                mOutputStream = mSocket.getOutputStream();
                mInputStream = mSocket.getInputStream();

                byte[] buffer = new byte[1024];
                while(true){
                    //在这里做读取服务端的数据
                }
            } catch (IOException e) {
                try {
                    mSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    @ReactMethod
    public void scanBT(){
        deviceList=new ArrayList<>();

        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
//        filter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mReceiver, filter);

        mAdapter = BluetoothAdapter.getDefaultAdapter();//获取蓝牙适配器，蓝牙的打开 关闭 搜索设备 都是通过蓝牙适配器来完成的。
        mAdapter.startDiscovery();
    }
    @ReactMethod
    public void connectBT(int index){
        BluetoothDevice mSerDevice=deviceList.get(index);
        int State = mSerDevice.getBondState(); //获取蓝牙设备的状态
        switch (State) {
            case BluetoothDevice.BOND_NONE://未配对
                //做配对处理
                Method createBondMethod;
                try {
                    createBondMethod = BluetoothDevice.class.getMethod("createBond");
                    createBondMethod.invoke(mSerDevice);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            case BluetoothDevice.BOND_BONDED://已配对
                //准备连接，下面根据需要选其中一种
                // connetServer();//以数据传输的方式连接
                connetServer(mSerDevice);//以语音通信的方式连接

            default:
                break;
        }
    }
    @ReactMethod
    public void initListenconn(){//监听连接
        new Thread(){
            public void run(){
                listener();
            }
        }.start();
    }
    public void listener(){
        String SPP_UUID ="10001105-0000-1000-8000-00805f9b34fb";//准备一个UUID码
        UUID uuid = UUID.fromString(SPP_UUID);
        BluetoothServerSocket ServerSocket;
        try {
            ServerSocket = mAdapter.listenUsingRfcommWithServiceRecord("BluetoothChatSecure",uuid);
            mSocket = ServerSocket.accept();//阻塞，，，，，，，
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();
            while(true){
                //这里可以读取客户端发送过来的数据
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
