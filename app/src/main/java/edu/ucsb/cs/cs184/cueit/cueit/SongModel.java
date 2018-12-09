package edu.ucsb.cs.cs184.cueit.cueit;


import java.io.Serializable;

public class SongModel implements Serializable, Comparable<SongModel>{

    private String songURL;
    private String songName;
    private long upVotes;
    private long Timestamp;


    public SongModel(String id, String name, long votes) {
        this.songURL = id;
        this.songName=name;
        this.upVotes=votes;
        this.Timestamp = 0;
    }

    public SongModel(String id, String name, long votes, long timestamp ) {
        this.songURL = id;
        this.songName=name;
        this.upVotes =votes;
        this.Timestamp = timestamp;
    }

    public String getSongURL() {
        return songURL;
    }

    public String getSongName() {
        return songName;
    }

    public long getUpVotes() {
        return upVotes;
    }

    public long getTimestamp() { return Timestamp; }

    public int compareTo (SongModel b) {
        if (b.getUpVotes() == getUpVotes())
            return (int) (getTimestamp() - b.getTimestamp());
        else
            return (int) (b.getUpVotes() - getUpVotes());
    }
}
