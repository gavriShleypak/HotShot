package com.example.yarden.hotshot.Activitys;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.os.Bundle;

import com.example.yarden.hotshot.R;
import com.example.yarden.hotshot.Utils.P2PWifi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class PeersProviderFragment extends Fragment {

    private ListView mPeersList;
    private MainActivity mAvtivity;
    private P2PWifi mP2PWifi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAvtivity = (MainActivity) getActivity();
        mP2PWifi = mAvtivity.getP2PWifi();
        mPeersList = (ListView) getActivity().findViewById(R.id.peers_fr_pr);
   //     mPeersList.setAdapter(mP2PWifi.getmPeersAdapter());
        exqListener();
    }

    private void exqListener(){
        mPeersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mP2PWifi.connectToDevice(i);
            }
        });
    }

    public PeersProviderFragment(){

    }



}
