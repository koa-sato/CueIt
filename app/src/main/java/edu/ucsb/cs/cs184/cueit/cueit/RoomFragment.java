package edu.ucsb.cs.cs184.cueit.cueit;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

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
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;


import com.google.android.youtube.player.YouTubePlayerView;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelSection;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.firebase.database.ChildEventListener;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.auth.AUTH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Text;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;


public class RoomFragment extends android.app.Fragment implements YouTubePlayer.OnInitializedListener{

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;

    private AutoCompleteTextView dropdownList;


    private YouTubePlayer player;

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

    private static final String PROPERTIES_FILENAME = "youtube.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 4;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_room, container, false);
        youTubeView = view.findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        searchBar = (EditText) view.findViewById(R.id.searchBar);
//        enterSong =(Button) view.findViewById(R.id.enterSong);
      //  search Bar = (EditText) view.findViewById(R.id.searchBar);
        enterSong =(Button) view.findViewById(R.id.enterSong);
        songsList =(ListView) view.findViewById(R.id.list);
        dropdownList = (AutoCompleteTextView) view.findViewById(R.id.dropDown);
        Log.d("TAG123", (dropdownList==null) +"");

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

            dropdownList.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    // do something when the button is clicked
                    // Yes we will handle click here but which button clicked??? We don't know

                    List<String> dropdownText = new ArrayList<>();


                    try {
                        // This object is used to make YouTube Data API requests. The last
                        // argument is required, but since we don't need anything
                        // initialized when the HttpRequest is initialized, we override
                        // the interface and provide a no-op function.
                        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),  new HttpRequestInitializer() {
                            public void initialize(HttpRequest request) throws IOException {
                            }
                        }).setApplicationName("youtube-cmdline-search-sample").build();

                        // Prompt the user to enter a query term.
                        String queryTerm = dropdownList.getText().toString();

                        // Define the API request for retrieving search results.
                        YouTube.Search.List search = youtube.search().list("id,snippet");

                        // Set your developer key from the {{ Google Cloud Console }} for
                        // non-authenticated requests. See:
                        // {{ https://cloud.google.com/console }}
                        ;
                        search.setKey(Config.YOUTUBE_API_KEY);
                        search.setQ(queryTerm);

                        // Restrict the search results to only include videos. See:
                        // https://developers.google.com/youtube/v3/docs/search/list#type
                        search.setType("video");

                        // To increase efficiency, only retrieve the fields that the
                        // application uses.
                        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

                        //System.out.println("HERE WE ARE");

                        // Call the API and print results.
                        SearchListResponse searchResponse = search.execute();
                        List<SearchResult> searchResultList = searchResponse.getItems();
                        System.out.println(searchResultList.toString());
                        if (searchResultList != null) {
                            dropdownText = prettyPrint(searchResultList.iterator(), queryTerm);

                        } else {
                            System.out.println("hello we r null");
                        }
                    } catch (GoogleJsonResponseException e) {
                        System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                                + e.getDetails().getMessage());
                    } catch (IOException e) {
                        System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_dropdown_item_1line, dropdownText);
                    dropdownList.setThreshold(3);
                    //Set the adapter
                    dropdownList.setAdapter(adapter);

                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {}


        });


        enterSong.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // do something when the button is clicked
                // Yes we will handle click here but which button clicked??? We don't know

                String str = dropdownList.getText().toString();
                String[] splited = str.split("\n");
                for(int i = 0; i<splited.length; i++)
                {
                    System.out.println(i + " "+ splited[i]);
                }

                splited[0] = splited[0].replace("#","");
                splited[0] = splited[0].replace(".","");
                splited[0] = splited[0].replace("$","");
                splited[0] = splited[0].replace("[","");
                splited[0] = splited[0].replace("]","");



                SongModel newSong = new SongModel (splited[1], splited[0], 1, System.currentTimeMillis());

                DatabaseReference db = FirebaseHelper.getInstance().getReference("Rooms");
                db.child(RoomFragment.roomCode).child ("songList").child (splited[1]).setValue (newSong).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("onClick", "Success!");
                    }
                });
                dropdownList.setText("");


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


    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */
    private static ArrayList<String> prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {
        ArrayList<String> titles = new ArrayList<>();

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                titles.add(singleVideo.getSnippet().getTitle()+"\n" +rId.getVideoId());
                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
        return titles;
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