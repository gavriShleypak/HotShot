package com.example.yarden.hotshot.Provider;


import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.ArrayAdapter;


//import com.example.yarden.hotshot.Utils.SendReceive;
import com.example.yarden.hotshot.Utils.P2PWifi;
import com.example.yarden.hotshot.Utils.User;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;

public class ShareWifi implements PeersEventListener, ConnectionEstablishedInterface  {

    private P2PWifi p2pWifi;
    private  WifiP2pManager mManager;
    private WifiManager m_wifiManager;
    private WifiConfiguration m_wifiConf;
    private static Method getWifiApState;
    private User user;
    private int Time_Out=10;
    private ArrayList<PeersEventListener> mPeersEventListeners;
    private ArrayAdapter<String> mPeersStringAdapter;


    public ShareWifi(P2PWifi _p2pWifi, WifiManager wifiManager, User _user)
    {
        m_wifiConf = new WifiConfiguration();
        p2pWifi = _p2pWifi;
       // user = _user;
        m_wifiManager= wifiManager;
        user = _user;
        mPeersEventListeners = new ArrayList<>();
        p2pWifi.setPeerEventListener(this);
        p2pWifi.setConnectionEstablishedEventListeners(this);


    }



    private void enableWifi(){
        if (!m_wifiManager.isWifiEnabled())
            m_wifiManager.setWifiEnabled(true);
    }

    public boolean CheckSetting()
    {
        if(user.GetHotsptPass() != null)
        {
            return true;
        }
        else return false;
    }


    @Override
    public void OnPeersAppearEvent(ArrayAdapter<String> adapter) {
        mPeersStringAdapter = adapter;
        notifyAllListeners();
    }

    private void notifyAllListeners(){
        for(PeersEventListener listener: mPeersEventListeners){
            listener.OnPeersAppearEvent(mPeersStringAdapter);
        }
    }

    public void setPeerEventListener(PeersEventListener i_listener){
        mPeersEventListeners.add(i_listener);
    }

    public String HotSpotConnectionInfo(){
        return "HT37--12345678";
    }

    @Override
    public void SendInfo(P2PWifi.SendReceive i_sendReceive) {
        p2pWifi.WriteMessege(HotSpotConnectionInfo());

        //or
        // i_sendReceive.write(HotSpotConnectionInfo().getBytes());
    }

    // empty constructor??
//    public void ShareWifi() {
//
    //    //int count = 0;
//        //boolean findClient = false;
//
//        try {
//
//            p2pWifi.StartDiscoveringP2P();
//
//            // cant send first need to establish p2p connection
//
    //   //    while (count == Time_Out) {
    //   //        wait(2000);
    //   //        if (p2pWifi.GetAnswerMsg() == "Hi") {
    //   //            p2pWifi.WriteMessege("HT37--12345678");//need to send also firebaseuser id
    //   //            findClient = true;
    //   //            break;
    //   //        }
    //   //        //trun on HotSpot!
    //   //        count++;
    //   //    }
    //    }
    //    catch (Exception e){
    //        e.printStackTrace();
    //    }
//    }
}
