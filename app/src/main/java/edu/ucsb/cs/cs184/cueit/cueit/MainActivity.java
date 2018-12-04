package edu.ucsb.cs.cs184.cueit.cueit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseHelper.Initialize(this);
        DatabaseReference db = FirebaseHelper.getInstance().getReference("Room");
        db.child("NewChild").setValue ("Hello World!");
        db.child("NewChild2").setValue ("Hello World2!");
    }
}
