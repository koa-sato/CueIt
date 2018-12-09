package edu.ucsb.cs.cs184.cueit.cueit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

class SongListAdapter extends ArrayAdapter<SongModel> {
    private Context mContext;
    private int mResource;
    private int position;
    public SongListAdapter(Context mContext, int resource, ArrayList<SongModel> songs) {
        super(mContext, resource, songs);
        this.mContext = mContext;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // super.getView(position, convertView, parent);
        String id = getItem(position).getSongURL();
        String songName = getItem(position).getSongName();
        long upvotes = getItem(position).getUpVotes();
        this.position = position;

        SongModel sm = new SongModel(id,songName, upvotes);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView songtxt = (TextView) convertView.findViewById(R.id.songTitle);
        songtxt.setText(songName);
        final TextView upvotesText = (TextView) convertView.findViewById(R.id.numLikes);
        upvotesText.setText(upvotes+" Upvotes");
        Button upvotesButton = (Button) convertView.findViewById(R.id.upvoteButton);
        upvotesButton.setOnClickListener(new LikeButtonListener(getItem (position)));

        return convertView;
    }


    class LikeButtonListener implements View.OnClickListener{
        SongModel songModel;
        public LikeButtonListener (SongModel songModel) {
            this.songModel = songModel;
        }

        public void onClick(View v) {

            SongModel newSong = new SongModel(songModel.getSongURL(), songModel.getSongName(), songModel.getUpVotes()+1, songModel.getTimestamp());

            DatabaseReference db = FirebaseHelper.getInstance().getReference("Rooms");
            db.child(RoomFragment.roomCode).child("songList").child(songModel.getSongURL()).setValue(newSong).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("onClick", "Success!");
                }
            });
        }
    }
}