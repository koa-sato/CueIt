package edu.ucsb.cs.cs184.cueit.cueit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button createRoomButton = findViewById(R.id.create_room_button);
        Button joinRoomButton = findViewById(R.id.join_room_button);


        FirebaseHelper.Initialize(this);
        FirebaseHelper.checkAndCreateRoom(new FirebaseHelper.OnCreateRoomSuccessListener() {
            @Override
            public void onSuccess(boolean success) {
                Log.d ("Room","Success!");
            }
        });
    }
}
