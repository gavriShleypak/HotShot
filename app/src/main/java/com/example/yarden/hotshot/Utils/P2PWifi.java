package com.example.yarden.hotshot.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.yarden.hotshot.Activitys.MainActivity;
import com.example.yarden.hotshot.Client.ClientClass;
import com.example.yarden.hotshot.Provider.ConnectionEstablishedInterface;
import com.example.yarden.hotshot.Provider.PeersEventListener;
import com.example.yarden.hotshot.Provider.ServerClass;


import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class P2PWifi implements Serializable {

    static final int MESSAGE_READ = 1;
    private WifiManager wifiManager;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private String[] deviceNameArray;
    private WifiP2pDevice[] deviceArray;
    private ServerClass serverClass;
    private ClientClass clientClass;
    private SendReceive sendReceive;
    private Context context;
    private MainActivity activity;
    private String answerMsg = null;
    private WifiP2pConfig config;

    private ArrayAdapter<String> mPeersAdapter;
    private ArrayList<PeersEventListener> peersEventListeners;
    private ArrayList<ConnectionEstablishedInterface> connectionEstablishedEventListeners;

    public P2PWifi(Context _context, MainActivity _activity, WifiP2pManager wifiP2pManager,
                   WifiP2pManager.Channel channel) {
        context = _context;
        activity = _activity;
        mManager = wifiP2pManager;
        config = new WifiP2pConfig();
        mChannel = channel;
        peersEventListeners = new ArrayList<>();
        connectionEstablishedEventListeners = new ArrayList<>();
    }

    public ArrayAdapter<String> getmPeersAdapter() {
        return mPeersAdapter;
    }

    // all initialize performed in main Activity.
    public void initialWork() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // move to main activity.
        //wifiManager= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        ////do when create the class and send in ctor
        //// mManager= (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        //mChannel=mManager.initialize(context,getMainLooper(),null);
        //mReceiver=new WifiClientBroadcastReceiver(mManager, mChannel,activity);
        //
        ////try change name
        //try {
        //    Method method = mManager.getClass().getMethod("setDeviceName",
        //            WifiP2pManager.Channel.class, String.class, WifiP2pManager.ActionListener.class);
        //
        //    method.invoke(mManager, mChannel, "HotShare", new WifiP2pManager.ActionListener() {
        //        @Override
        //        public void onSuccess(){
        //            Log.d("setDeviceName succeeded", "true");
        //        }
        //        @Override
        //        public void onFailure(int reason) {
        //            Log.d("setDeviceName failed", "true");
        //        }
        //    });
        //} catch (Exception e){Log.d("setDeviceName exception", "true");}
        //
        //mIntentFilter=new IntentFilter();
        //mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        //mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        //mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        //mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public void StartDiscoveringP2P() throws InterruptedException {

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "Searching", Toast.LENGTH_SHORT).show();
                Log.d("Discovery started", "success");
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                Log.d("Discovery Failed", "fail");
            }
        });

        // discover function only start to discover
        //    if(SerchForDevice()) {
        //     mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
        //         @Override
        //         public void onSuccess() {
        //             Log.d("connect", "connect");
        //         }
        //
        //         @Override
        //         public void onFailure(int i) {
        //             Log.d("Notconnect", "Notconnect");
        //         }
        //     });
        //     return true; //connect
        // }
        // else {return false;}
    }

    public void WriteMessege(String msg) {
        sendReceive.write(msg.getBytes());
    }

    public String GetAnswerMsg() {
        return answerMsg;
    }

    public void connectToDevice(int i){
        final WifiP2pDevice device=deviceArray[i];
        WifiP2pConfig config=new WifiP2pConfig();
        config.deviceAddress=device.deviceAddress;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(context,"Connected to "+device.deviceName,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(context,"Not Connected",Toast.LENGTH_SHORT).show();
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    answerMsg = new String(readBuff, 0, msg.arg1);
                    // send the data to Client
            }
            return true;
        }
    });

    // Events

    private void notifyAllPeersListeners(){
        for(PeersEventListener listener: peersEventListeners){
            listener.OnPeersAppearEvent(mPeersAdapter);
        }
    }

    public void setPeerEventListener(PeersEventListener i_listener){
        peersEventListeners.add(i_listener);
    }

    private void notifyAllConnectionListeners(){
        for(ConnectionEstablishedInterface listener : connectionEstablishedEventListeners ){
            listener.SendInfo(sendReceive);
        }
    }

    public void setConnectionEstablishedEventListeners(ConnectionEstablishedInterface i_listener){
        connectionEstablishedEventListeners.add(i_listener);
    }

    //Listeners

    public WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if (!peerList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index = 0;

                for (WifiP2pDevice device : peerList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }

                mPeersAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, deviceNameArray);
            }

            // if we have data notify listeners.
            if (peers.size() == 0) {
                Toast.makeText(context, "No Device Found", Toast.LENGTH_SHORT).show();
                return;
            }else{
                notifyAllPeersListeners();
            }
        }
    };

    public WifiP2pManager.ConnectionInfoListener connectionInfoListener =
            new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                    final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;

                    if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) // Host
                    {
                        serverClass = new ServerClass(sendReceive);
                        serverClass.start();
                    } else if (wifiP2pInfo.groupFormed) // Client
                    {
                        clientClass = new ClientClass(groupOwnerAddress, sendReceive);
                        clientClass.start();
                    }
                }
            };

    // method never in use.
    //private boolean SerchForDevice() {
    //
    //    boolean findDevice = false;
    //    while (!findDevice && deviceArray != null) /// do timeout if not found device after 4 min
    //
    //        for (WifiP2pDevice device : deviceArray) {
    //            if (device.deviceName == "HotShsre") {
    //                config.deviceAddress = device.deviceAddress;
    //                findDevice = true;
    //            }
    //        }
    //
    //    return findDevice;
    //}

}
