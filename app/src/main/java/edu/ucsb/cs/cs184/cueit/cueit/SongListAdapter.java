package edu.ucsb.cs.cs184.cueit.cueit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

class SongListAdapter extends ArrayAdapter<SongModel> {
    private Context mContext;
    private int mResource;
    public SongListAdapter(Context mContext, int resource, ArrayList<SongModel> songs) {
        super(mContext, resource, songs);
        this.mContext = mContext;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       // super.getView(position, convertView, parent);
        String id = getItem(position).getSongID();
        String songName = getItem(position).getName();
        int upvotes =  getItem(position).getLikes();

        SongModel sm = new SongModel(id,songName, upvotes);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView songtxt = (TextView) convertView.findViewById(R.id.songTitle);
        songtxt.setText(songName);
        final TextView upvotesText = (TextView) convertView.findViewById(R.id.numLikes);
        upvotesText.setText(upvotes+" Upvotes");
        Button upvotesButton = (Button) convertView.findViewById(R.id.upvoteButton);
        upvotesButton.setOnClickListener(  new View.OnClickListener() {
            public void onClick(View v) {
                // do something when the button is clicked
                // Yes we will handle click here but which button clicked??? We don't know
                String numVotes = upvotesText.getText().toString();
                String[] splited = numVotes.split(" ");

                upvotesText.setText(Integer.parseInt(splited[0])+" Upvotes");

            }
        });

        return convertView;

    }
}
