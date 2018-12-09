package edu.ucsb.cs.cs184.cueit.cueit;

import android.os.Bundle;
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

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

public class RoomFragment extends android.app.Fragment implements YouTubePlayer.OnInitializedListener{

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private YouTubePlayer player;
    private EditText searchBar;
    private Button enterSong;
    private TextView tv;
    private ListView songsList;
    private ArrayList<SongModel> songs= new ArrayList<SongModel>();
    private String roomCode;
    private SongListAdapter sladapter;

    private MyPlayerStateChangeListener playerStateChangeListener;


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

        enterSong.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // do something when the button is clicked
                        // Yes we will handle click here but which button clicked??? We don't know
                        System.out.println("HELLO THSI IS IT "+ searchBar.getText());

                    }
                });

        songs.add(new SongModel("FZfjWXYm80k", "Papparai",2));
        songs.add(new SongModel("asb1e", "Turn down",1));
        songs.add(new SongModel("afs1e", "Turn up",1));
        songs.add(new SongModel("sjdf", "Turn right",1));

        sladapter = new SongListAdapter(getActivity().getApplicationContext(), R.layout.list_item, songs);
        songsList.setAdapter(sladapter);

        playerStateChangeListener = new MyPlayerStateChangeListener();

        return view;
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        this.player = player;
//        player.setPlaylistEventListener(playlistEventListener);
        player.setPlayerStateChangeListener(playerStateChangeListener);
//        player.setPlaybackEventListener(playbackEventListener);
        if (!wasRestored) {
            player.loadVideo("fhWaJi1Hsfo"); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo

        }
//        setControlsEnabled(true);
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
            Log.e("fck", "STOPPED");
            //get next queued item, if exists
            if (songs.get(0) == null) {
                //ERROR!
                String error = String.format("this is bad", "nullington palace");
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                return;
            }
            String next_video;
            next_video = songs.get(0).songID;
            //erase the first child from the songs queue
            songs.remove(0); //this pushes down right?
            //notify the adapter of the change
            sladapter.notifyDataSetChanged();
            player.loadVideo(next_video);
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason reason) {

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