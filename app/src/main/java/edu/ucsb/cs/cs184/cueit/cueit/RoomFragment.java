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

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RoomFragment extends android.app.Fragment implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private EditText searchBar;
    private Button enterSong;
    private TextView tv;
    private ListView songsList;
    private String roomCode;
    private boolean isMaster;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setContentView(R.layout.activity_room);
        final View view = inflater.inflate(R.layout.fragment_room, container, false);
        youTubeView = view.findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
//        searchBar = (EditText) view.findViewById(R.id.searchBar);
//        enterSong =(Button) view.findViewById(R.id.enterSong);
        searchBar = (EditText) view.findViewById(R.id.searchBar);
        enterSong =(Button) view.findViewById(R.id.enterSong);
        songsList =(ListView) view.findViewById(R.id.list);


        roomCode = getArguments().getString("roomId");
        Log.d ("onChildChanged", roomCode);


        FirebaseHelper.getInstance().getReference("Rooms").child(roomCode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wInfo = wifiManager.getConnectionInfo();
                        String macAddress = wInfo.getMacAddress();
                        isMaster = dataSnapshot.child ("MasterDevice").getValue().toString().equals(macAddress);
                        Log.d ("onChildAdd", isMaster+"");


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        enterSong.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // do something when the button is clicked
                // Yes we will handle click here but which button clicked??? We don't know

                DatabaseReference db = FirebaseHelper.getInstance().getReference("Rooms");
                db.child(roomCode).child ("songList").child (searchBar.getText().toString())
                        .child("Timestamp").setValue (System.currentTimeMillis());
                db.child(roomCode).child ("songList").child (searchBar.getText().toString())
                        .child("upVotes").setValue (1);
                //db.child(roomCode).child ("songList").child (searchBar.getText().toString())
                //        .child("upVotes").setValue (1);
            }
        });

        ArrayList<SongModel> songs = new ArrayList<>();
        songs.add(new SongModel("asbe", "Papparai",2));
        songs.add(new SongModel("asb1e", "Turn down",1));
        songs.add(new SongModel("afs1e", "Turn up",1));
        songs.add(new SongModel("sjdf", "Turn right",1));

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