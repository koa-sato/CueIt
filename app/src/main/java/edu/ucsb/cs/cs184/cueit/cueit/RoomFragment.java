package edu.ucsb.cs.cs184.cueit.cueit;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RoomFragment extends android.app.Fragment implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private EditText searchBar;
    private Button enterSong;
    private TextView tv;
    private ListView songsList;
    public static String roomCode;
    private boolean isMaster;
    private ArrayList<SongModel> songs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_room, container, false);
        youTubeView = view.findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
        searchBar = (EditText) view.findViewById(R.id.searchBar);
        enterSong =(Button) view.findViewById(R.id.enterSong);
        songsList =(ListView) view.findViewById(R.id.list);


        RoomFragment.roomCode = getArguments().getString("roomId");

        songs = new ArrayList<>();
        Log.d ("onChildChanged", RoomFragment.roomCode);


        FirebaseHelper.getInstance().getReference("Rooms").child(RoomFragment.roomCode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wInfo = wifiManager.getConnectionInfo();
                        String macAddress = wInfo.getMacAddress();
                        isMaster = dataSnapshot.child ("MasterDevice").getValue().toString().equals(macAddress);
                        Log.d ("onChildAdd", isMaster+"");

                        songs.clear();
                        for (DataSnapshot song : dataSnapshot.child ("songList").getChildren()) {
                            Log.d ("onChildAdd", ""+song.child("Timestamp").getValue());
                            songs.add (new SongModel((String)song.child("songURL").getValue(),
                                    (String)song.child("songName").getValue(),
                                    (long)song.child("upVotes").getValue(),
                                    (long)song.child("timestamp").getValue()));

                        }
                        Collections.sort (songs);
                        SongListAdapter sladapter = new SongListAdapter(
                                getActivity().getApplicationContext(), R.layout.list_item, songs);
                        songsList.setAdapter(sladapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        enterSong.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // do something when the button is clicked
                // Yes we will handle click here but which button clicked??? We don't know
                String childName = searchBar.getText().toString();
                SongModel newSong = new SongModel (childName, childName, 1, System.currentTimeMillis());

                DatabaseReference db = FirebaseHelper.getInstance().getReference("Rooms");
                db.child(RoomFragment.roomCode).child ("songList").child (childName).setValue (newSong).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("onClick", "Success!");
                    }
                });

            }
        });

        SongListAdapter sladapter = new SongListAdapter(getActivity().getApplicationContext(), R.layout.list_item, songs);
        songsList.setAdapter(sladapter);

        return view;
    }



    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo("fhWaJi1Hsfo"); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(getActivity(), RECOVERY_REQUEST).show();
        } else {
            String error = String.format("this is bad", errorReason.toString());
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }



    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    } */

    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }
}