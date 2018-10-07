package com.example.yarden.hotshot.Provider;



import com.example.yarden.hotshot.Utils.SendReceive;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClass extends Thread{
    Socket socket;
    ServerSocket serverSocket;
    SendReceive sendReceive;


    public ServerClass(SendReceive i_sendReceive){
        sendReceive = i_sendReceive;
    }

    @Override
    public void run() {
        try {
            serverSocket=new ServerSocket(8888);
            socket=serverSocket.accept();
            sendReceive = new SendReceive(socket);
            sendReceive.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}