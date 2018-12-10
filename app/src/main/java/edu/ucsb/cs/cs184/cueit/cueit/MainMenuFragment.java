package edu.ucsb.cs.cs184.cueit.cueit;

import android.content.Context;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainMenuFragment extends android.app.Fragment {

    Button createRoomButton;
    Button joinRoomButton;
    ImageView imageView;
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
        imageView = view.findViewById(R.id.title_screen1);
        imageView.setImageResource(R.drawable.cue_it_large);

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

       // TextView tx = view.findViewById(R.id.title_screen);

//        Typeface font1 = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/title_font.TTF");
//        Typeface font2 = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/Muli-Regular.ttf");
//        tx.setTypeface(font1);
//        TextView a = view.findViewById(R.id.join_gang_button), b = view.findViewById(R.id.create_gang_button);
////        a.setTypeface(font2);
    }


    public void createRoom () {
        FirebaseHelper.checkAndCreateRoom(new FirebaseHelper.OnCreateRoomSuccessListener() {
            @Override
            public void onSuccess(String code) {
//                WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                WifiInfo wInfo = wifiManager.getConnectionInfo();
                String macAddress = getMacAddr();
                FirebaseHelper.updateRoom(code, "MasterDevice", macAddress);

                startRoomFragment(code);
            }
        });

    }


    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public void startRoomFragment(String code) {
        RoomFragment newFragment = new RoomFragment();
        Bundle args = new Bundle();
        args.putString ("roomId", code);
        newFragment.setArguments(args);

        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        //        FirebaseHelper.Room room = new FirebaseHelper.Room(code);

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
