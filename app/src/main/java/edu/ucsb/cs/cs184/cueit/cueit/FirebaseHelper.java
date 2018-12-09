package edu.ucsb.cs.cs184.cueit.cueit;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;


//import java.util.HashMap;

/**
 * Created by Donghao Ren on 03/11/2017.
 * Modified by Ehsan Sayyad on 11/9/2018
 */

/**
 * This is a Firebase helper starter class we have created for you
 * In your Activity, please call FirebaseHelper.Initialize() to setup the Firebase
 * Put your application logic in Initialize() and additional methods of your choosing.
 */
public class FirebaseHelper {
    /**
     * This is a message data structure that mirrors our Firebase data structure for your convenience
     */
    public static class Room implements Serializable {

        // Not working
        public static String code;
        public static String master;

        public Room(String code) {
            this.code = code;
        }

        public static String getCode() {
            return code;
        }

        public static String getMaster() { return master; }
    }

    /**
     * The Firebase database object
     */
    private static FirebaseDatabase db;


    /**
     * This is called once we initialize the firebase database object
     */
    public static void Initialize(Context context) {
        FirebaseApp.initializeApp(context);
        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference("Rooms");
        Log.d ("read", myRef.toString());
        // Your code should handle post added, post updated, and post deleted events.
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Object a = dataSnapshot.getValue();
                    if (a != null)
                        Log.d("read", "--------dsd------------" + a.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static FirebaseDatabase getInstance() {
        return db;
    }


    public static void checkAndCreateRoom (OnCreateRoomSuccessListener listener) {
        String room = (int)(Math.random()*10000) +"";
        checkRoom (room, new DatabaseGuessRoomListener(room, listener));
    }


    public static void createRoom (String roomID) {
        DatabaseReference db = FirebaseHelper.getInstance().getReference("Rooms");
        db.child(roomID).child ("Timestamp").setValue(System.currentTimeMillis());

    }

    public static void updateRoom (String roomId, String key, String value) {
        DatabaseReference db = FirebaseHelper.getInstance().getReference("Rooms");
        db.child(roomId).child (key).setValue(value);
    }


    public static void checkRoom (String roomID, CheckRoomListener roomListener) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Rooms");
        rootRef.addListenerForSingleValueEvent(new DatabaseCheckRoomListener(roomID, roomListener));
    }

    interface OnCreateRoomSuccessListener {
        public void onSuccess (String code);
    }

}

class DatabaseGuessRoomListener implements CheckRoomListener {
    String roomID;
    FirebaseHelper.OnCreateRoomSuccessListener listener;

    DatabaseGuessRoomListener (String roomID, FirebaseHelper.OnCreateRoomSuccessListener listener) {
        this.roomID = roomID;
        this.listener = listener;
    }

    public void onResponse (boolean roomExists) {
        if (!roomExists) {
            FirebaseHelper.createRoom(roomID);
            listener.onSuccess(roomID);
        }
        else {
            String room = (int)(Math.random()*10000)+"";
            FirebaseHelper.checkRoom (room, new DatabaseGuessRoomListener(room, listener));
        }
    }
}

class DatabaseCheckRoomListener implements ValueEventListener {
    String roomID;
    CheckRoomListener checkRoomListener;

    DatabaseCheckRoomListener (String roomID, CheckRoomListener checkRoomListener) {
        this.roomID = roomID;
        this.checkRoomListener = checkRoomListener;
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        checkRoomListener.onResponse(snapshot.hasChild(roomID));
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}