package com.example.yarden.hotshot.Client;



import com.example.yarden.hotshot.Utils.SendReceive;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientClass extends Thread{
    Socket socket;
    String hostAdd;
    SendReceive sendReceive;

    public  ClientClass(InetAddress hostAddress, SendReceive i_sendReceive)
    {
        hostAdd=hostAddress.getHostAddress();
        socket=new Socket();
        sendReceive = i_sendReceive;
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAdd,8888),500);
            sendReceive=new SendReceive(socket);
            sendReceive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}