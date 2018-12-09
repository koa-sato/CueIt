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
import android.widget.Adapter;
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

import org.w3c.dom.Text;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class RoomFragment extends android.app.Fragment implements YouTubePlayer.OnInitializedListener{

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private YouTubePlayer player;
    private EditText searchBar;
    private Button enterSong;
    private TextView tv;
    private ListView songsList;
    private TextView currentlyPlayingTextView;
    private TextView roomCodeTextView;

    private SongListAdapter sladapter;
    private MyPlayerStateChangeListener playerStateChangeListener;

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
        currentlyPlayingTextView = (TextView) view.findViewById(R.id.nowPlayingText);
        roomCodeTextView = (TextView) view.findViewById(R.id.roomCode);

        RoomFragment.roomCode = getArguments().getString("roomId");

        songs = new ArrayList<>();
        Log.d ("onChildChanged", RoomFragment.roomCode);

        roomCodeTextView.setText ("Room Code: " +RoomFragment.roomCode);

        FirebaseHelper.getInstance().getReference("Rooms").child(RoomFragment.roomCode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                        WifiInfo wInfo = wifiManager.getConnectionInfo();
                        String macAddress = getMacAddr();
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

                        if (player != null && !player.isPlaying()) {
                            playNextVideo();
                        }

                        try {
                            String curPlaying = (String) dataSnapshot.child("CurrentlyPlaying")
                                    .child("songName").getValue();
                            if (curPlaying != null)
                                currentlyPlayingTextView.setText ("Currently Playing: " + curPlaying);
                            else
                                currentlyPlayingTextView.setText ("Currently Playing: None");
                        } catch (Exception e) {
                            currentlyPlayingTextView.setText ("Currently Playing: None");
                        }

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

        sladapter = new SongListAdapter(getActivity().getApplicationContext(), R.layout.list_item, songs);
        songsList.setAdapter(sladapter);

        playerStateChangeListener = new MyPlayerStateChangeListener();

        return view;
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

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        this.player = player;
//        player.setPlaylistEventListener(playlistEventListener);
        player.setPlayerStateChangeListener(playerStateChangeListener);
//        player.setPlaybackEventListener(playbackEventListener);

        playNextVideo();
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

    public void refreshQueue() {
        //TODO sort the stuff with the highest likes at the top
        songsList.removeAllViews();
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {
        String playerState = "UNINITIALIZED";



        @Override
        public void onLoading() {
        }

        @Override
        public void onLoaded(String videoId) {
        }

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onVideoStarted() {
        }

        @Override
        public void onVideoEnded() {
            playNextVideo();
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason reason) {

        }
    }

    public void playNextVideo () {
        if (!songs.isEmpty()) {
            SongModel s = songs.get(0);
            String next_video = s.getSongURL();
            //erase the first child from the songs queue
            //notify the adapter of the change
            sladapter.notifyDataSetChanged();
            //TODO remove the next_video from the db so it doesnt show up in the listview

            DatabaseReference db = FirebaseHelper.getInstance().getReference("Rooms");
            db.child(RoomFragment.roomCode).child("songList").child(next_video).removeValue();

            db.child(RoomFragment.roomCode).child("CurrentlyPlaying").setValue(s);

            songs.remove(0); //this pushes down right?
            player.loadVideo(next_video);
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