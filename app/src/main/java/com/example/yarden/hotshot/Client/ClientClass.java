package com.example.yarden.hotshot.Client;



import com.example.yarden.hotshot.Utils.SendReceive;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import android.os.Handler;

public class ClientClass extends Thread{
    Socket socket;
    String hostAdd;
    SendReceive sendReceive;
    private boolean isSendReciveNull = true;

    public  ClientClass(InetAddress hostAddress)
    {
        hostAdd=hostAddress.getHostAddress();
        socket=new Socket();
     //   sendReceive = i_sendReceive;
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAdd,8888),500);
            sendReceive=new SendReceive(socket);
            isSendReciveNull = false;
            sendReceive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean IsSendReciveNull(){
        return isSendReciveNull;
    }

    public SendReceive getSendReceive() {
        return sendReceive;
    }
}