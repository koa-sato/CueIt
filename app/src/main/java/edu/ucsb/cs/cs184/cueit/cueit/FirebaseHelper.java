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

        public String author;
        public String content;
        public double timestamp;
        public double longitude;
        public double latitude;
        public String type;
        public int likes;

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
        DatabaseReference myRef = db.getReference("Room");
        Log.d ("read", myRef.toString());
        // Your code should handle post added, post updated, and post deleted events.
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object a = dataSnapshot.getValue();
                Log.d ("read", "--------dsd------------" +a.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static FirebaseDatabase getInstance() {
        return db;
    }

}