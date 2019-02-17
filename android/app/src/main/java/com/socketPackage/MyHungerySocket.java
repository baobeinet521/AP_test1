package com.socketPackage;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

public class MyHungerySocket{
    private static Socket socket1;

    private MyHungerySocket() {
    }
    public static Socket getSocket(String ip,int port) throws IOException {
        try{
            socket1.sendUrgentData(0xFF);
        }catch (Exception e1){
            synchronized (MyHungerySocket.class){
                try{
                    socket1.sendUrgentData(0xFF);
                }catch (Exception e2){
                    try {
                        socket1=new Socket(ip,port);
                    }catch (Exception e3){

                    }
                }
            }
        }finally {
            return socket1;
        }
    }
}
