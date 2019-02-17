package com.socketPackage;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SendThread extends Thread {
    private Activity activity;
    Handler handler;
    Socket s=null;
    String message;
    String ip; //远方服务器的IP地址
    int port;//远方服务器的port
    public SendThread(Activity activity, Handler handler, Socket s,String ip, int port, String message){
        this.activity = activity;
        this.handler = handler;
        this.message = message;
        this.ip = ip;
        this.port = port;
        this.s=s;
    }
    public void run(){
        //向远方发起TCP连接
        try {
            //第二个参数为True则为自动flush
//            PrintWriter out = new PrintWriter(
//                    new BufferedWriter(new OutputStreamWriter(
//                            s.getOutputStream())), true);
//            out.println(message);
            DataOutputStream writer = new DataOutputStream(s.getOutputStream());
            if(TextUtils.isEmpty(message)){
                writer.writeUTF("嘿嘿，你好啊，服务器.."); // 写一个UTF-8的信息
            }else{
                writer.writeUTF(message); // 写一个UTF-8的信息
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity,"发送成功", Toast.LENGTH_LONG).show();
                }
            });


        } catch (Exception e1){
            //e1.printStackTrace();
            final String errMsg=e1.getMessage();
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(activity,"发送失败："+errMsg, Toast.LENGTH_LONG).show();
                }
            });
        }finally {
//            try {
//                s.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
    public void reveive(){

    }
}
