package edu.ucsb.cs.cs184.cueit.cueit;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainMenuFragment extends android.app.Fragment {

    Button createRoomButton;
    Button joinRoomButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        createRoomButton = view.findViewById(R.id.create_room_button);
        joinRoomButton = view.findViewById(R.id.join_room_button);

        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startCreateRoomFragment(v);
                createRoom();
            }
        });

        joinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJoinRoomFragment(v);
            }
        });
    }


    public void createRoom () {
        FirebaseHelper.checkAndCreateRoom(new FirebaseHelper.OnCreateRoomSuccessListener() {
            @Override
            public void onSuccess(String code) {
                WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                String macAddress = wInfo.getMacAddress();
                FirebaseHelper.updateRoom(code, "MasterDevice", macAddress);

                startRoomFragment(code);
            }
        });

    }

    public void startRoomFragment(String code) {
        RoomFragment newFragment = new RoomFragment();
        Bundle args = new Bundle();
        newFragment.setArguments(args);
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        FirebaseHelper.Room room = new FirebaseHelper.Room(code);

        transaction.commit();
    }


    public void startJoinRoomFragment(View v) {
        JoinRoomFragment newFragment = new JoinRoomFragment();
        Bundle args = new Bundle();
        newFragment.setArguments(args);

        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }
}
