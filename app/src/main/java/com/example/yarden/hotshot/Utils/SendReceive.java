package com.example.yarden.hotshot.Utils;


import android.os.Handler;
import android.widget.Toast;

import com.example.yarden.hotshot.Utils.P2PWifi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SendReceive extends Thread{
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private android.os.Handler handler;
    public SendReceive(Socket skt)
    {
        socket=skt;
        try {
            inputStream=socket.getInputStream();
            outputStream=socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer=new byte[1024];
        int bytes;

        while(true){
            if(handler != null)
                break;
        }

        while (socket!=null)
        {
            try {
                bytes=inputStream.read(buffer);
                if(bytes>0)
                {
                    handler.obtainMessage(1,bytes,-1,buffer).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void write(byte[] bytes)
    {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
