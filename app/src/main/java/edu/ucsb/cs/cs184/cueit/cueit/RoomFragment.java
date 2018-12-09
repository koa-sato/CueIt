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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
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

public class RoomFragment extends android.app.Fragment implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private AutoCompleteTextView dropdownList;

    private Button enterSong;
    private TextView tv;
    private ListView songsList;
    private String roomCode;
    private boolean isMaster;


    private static final String PROPERTIES_FILENAME = "youtube.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 8;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setContentView(R.layout.activity_room);
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
                    dropdownList.setThreshold(1);
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


                DatabaseReference db = FirebaseHelper.getInstance().getReference("Rooms");
                db.child(roomCode).child ("songList").child (splited[0])
                        .child("Timestamp").setValue (System.currentTimeMillis());
                db.child(roomCode).child ("songList").child (splited[1])
                        .child("upVotes").setValue (1);
                //db.child(roomCode).child ("songList").child (dropdownList.getText().toString())
                //        .child("upVotes").setValue (1);
                dropdownList.setText("");
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

    private static String getInputQuery() throws IOException {

        String inputQuery = "";

        System.out.print("Please enter a search term: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        inputQuery = bReader.readLine();

        if (inputQuery.length() < 1) {
            // Use the string "YouTube Developers Live" as a default.
            inputQuery = "YouTube Developers Live";
        }
        return inputQuery;
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