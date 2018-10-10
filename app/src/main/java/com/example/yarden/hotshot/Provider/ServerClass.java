package com.example.yarden.hotshot.Provider;



import com.example.yarden.hotshot.Utils.SendReceive;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Handler;

public class ServerClass extends Thread{
    Socket socket;
    ServerSocket serverSocket;
    SendReceive sendReceive;
    private boolean isSendReciveNull = true;

    public ServerClass(){
      //  sendReceive = i_sendReceive;
    }

    @Override
    public void run() {
        try {
            serverSocket=new ServerSocket(8888);
            socket=serverSocket.accept();
            sendReceive = new SendReceive(socket);
            sendReceive.start();
            isSendReciveNull = false;

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