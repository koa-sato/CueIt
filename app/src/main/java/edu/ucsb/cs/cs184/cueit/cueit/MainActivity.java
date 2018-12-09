package edu.ucsb.cs.cs184.cueit.cueit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private EditText searchBar;
    private Button enterSong;
    private ListView songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_room);
        setContentView(R.layout.activity_room2);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
//        searchBar = (EditText) findViewById(R.id.searchBar);
//        enterSong =(Button) findViewById(R.id.enterSong);
        searchBar = (EditText) findViewById(R.id.searchBar);
        enterSong =(Button) findViewById(R.id.enterSong);
        songsList =(ListView) findViewById(R.id.list);

        enterSong.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // do something when the button is clicked
                        // Yes we will handle click here but which button clicked??? We don't know
                        System.out.println("HELLO THSI IS IT "+ searchBar.getText());

                    }
                });

        ArrayList<SongModel> songs = new ArrayList<>();
        songs.add(new SongModel("asbe", "Papparai",2));
        songs.add(new SongModel("asb1e", "Turn down",1));
        songs.add(new SongModel("afs1e", "Turn up",1));
        songs.add(new SongModel("sjdf", "Turn rgiht",1));

        SongListAdapter sladapter = new SongListAdapter(this, R.layout.list_item, songs);
        songsList.setAdapter(sladapter);

    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo("luHhcfvCw9g"); // Plays https://www.youtube.com/watch?v=luHhcfvCw9g
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format("this is bad", errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }
}