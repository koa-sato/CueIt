package edu.ucsb.cs.cs184.cueit.cueit;


public class SongModel {

    String songID;
    String songName;
    int likes;


    public SongModel(String id, String name, int votes ) {
        this.songID = id;
        this.songName=name;
        this.likes=votes;
    }

    public String getSongID() {
        return songID;
    }

    public String getName() {
        return songName;
    }

    public int getLikes() {
        return likes;
    }


}
