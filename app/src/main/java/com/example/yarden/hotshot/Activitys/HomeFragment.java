package com.example.yarden.hotshot.Activitys;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yarden.hotshot.Client.AskForWifi;
import com.example.yarden.hotshot.Provider.ConnectionEstablishedInterface;
import com.example.yarden.hotshot.Provider.PeersEventListener;
import com.example.yarden.hotshot.Provider.ShareWifi;
import com.example.yarden.hotshot.R;
import com.example.yarden.hotshot.Utils.SendReceive;
import com.example.yarden.hotshot.Utils.P2PWifi;
import com.example.yarden.hotshot.Utils.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class HomeFragment extends Fragment implements PeersEventListener {
    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private SendReceive sendReceive;
    private P2PWifi p2PWifi;
    private AskForWifi getWifi;
    private ShareWifi shareWifi;
    private FirebaseUser currectUser;
    private MainActivity mainActivity;
    private FirebaseDatabase database ;
    private DatabaseReference myRef;
    private FragmentActivity myFRContext;
    private boolean mIsClient;
    private boolean mPermissionsGranted;

    // constant
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home , container , false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity)getActivity();
        init();
    }

    private void init()
    {
        // should consider to move to main activity.
         database = FirebaseDatabase.getInstance();
         myRef = database.getReference("message");

         p2PWifi = mainActivity.getP2PWifi();

        ListView list = (ListView)getActivity().findViewById(R.id.fr_listvw);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              try{  p2PWifi.connectToDevice(i);
            }catch (Exception e){
                  e.printStackTrace();
              }
            }
        });

       /// try {
       /// wifiManager= (WifiManager) mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
       /// wifiP2pManager = (WifiP2pManager) mainActivity.getSystemService(Context.WIFI_P2P_SERVICE);
       /// p2PWifi = new P2PWifi(mainActivity.getApplicationContext(),(MainActivity) mainActivity , wifiP2pManager);
       ///
       ///     p2PWifi.initialWork();
       /// } catch (NoSuchMethodException e) {
       ///     e.printStackTrace();
       /// } catch (IllegalAccessException e) {
       ///     e.printStackTrace();
       /// } catch (InvocationTargetException e) {
       ///     e.printStackTrace();
       /// }catch (Exception e){
       ///     e.printStackTrace();
       /// }

       // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("TAG", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        User user = new User(currectUser);
        getWifi = new AskForWifi(p2PWifi , wifiManager );
        shareWifi = new ShareWifi(p2PWifi , wifiManager , user);
        shareWifi.setPeerEventListener(this);

        FloatingActionButton fab_getWifi = (FloatingActionButton) mainActivity.findViewById(R.id.fab_getWifi);
        fab_getWifi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mIsClient = true;
            try{
                // here request permission
               checkPermissionsAndAction();
            }
            catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //fab_getWifi.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Toast.makeText(getActivity(), "Searching for available Wifi",
        //                Toast.LENGTH_LONG).show();
        //        if(getWifi.GetWifi()){
        //            Toast.makeText(getActivity(), "Connect",
        //                    Toast.LENGTH_LONG).show();
        //        }
        //        else{
        //            Toast.makeText(getActivity(), "No available Device, try agin later ",
        //                    Toast.LENGTH_LONG).show();
        //        }
        //    }
        //});

        FloatingActionButton fab_shareWifi = (FloatingActionButton) mainActivity.findViewById(R.id.fab_shareWifi);
        fab_shareWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsClient = false;
                try {
                   checkPermissionsAndAction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //fab_shareWifi.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Toast.makeText(getActivity(), "Searching for client",
        //                Toast.LENGTH_LONG).show();
        //        if(shareWifi.ShareWifi()){
        //            Toast.makeText(getActivity(), "Start HotSpot",
        //                    Toast.LENGTH_LONG).show();
        //        }
        //        else{
        //            Toast.makeText(getActivity(), "No available Device, try agin later",
        //                    Toast.LENGTH_LONG).show();
        //        }

             /*   if(shareWifi.CheckSetting()) {
                    Snackbar.make(view, "Searching for users", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    shareWifi.ShareWifi();
                }
                else
                {
                    Snackbar.make(view, "Password not available", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //Intent profileIntent = new Intent(MainActivity.this , ProfileFragment.class);
                    //profileIntent.putExtra("FillPassword", true);
               //     startActivity(profileIntent);
                }*/
    //        }
    //    });
    //

    }

    @Override
    public void OnPeersAppearEvent(ArrayAdapter<String> adapter) {

        ListView listV = (ListView) getActivity().findViewById(R.id.fr_listvw);
        listV.setAdapter(p2PWifi.getmPeersAdapter());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mPermissionsGranted = true;
                    p2PWifi.StartDiscoveringP2P(mIsClient);

                }else{
                    mPermissionsGranted = false;
                }
            }
        }
    }

    private boolean isPermissionsGranted(String[] permissions){

        boolean res = true;
        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(mainActivity.getApplicationContext(),
                    permission)!= PackageManager.PERMISSION_GRANTED){
                res =false;
                break;
            }
        }

        return res;
    }

    private void checkPermissionsAndAction(){
        if(!isPermissionsGranted(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET})){
            if(ActivityCompat.shouldShowRequestPermissionRationale(mainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)){
                Toast.makeText(mainActivity.getApplicationContext(), "Need this permission to find nearby wifi", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(mainActivity, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.INTERNET},MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }else{
            p2PWifi.StartDiscoveringP2P(mIsClient);
        }
    }

}
