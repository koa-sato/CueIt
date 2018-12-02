package edu.ucsb.cs.cs184.cueit.cueit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by deni on 11/19/18.
 */

public class SelectOptionActivity extends AppCompatActivity {
    Button create;
    Button join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option);

        create = findViewById(R.id.create_gang_button);
        join = findViewById(R.id.join_gang_button);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                becomeMaster();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call the join group activity
                joinRoom();
            }
        });
    }

    private void becomeMaster(){
        startActivity(new Intent(this, RoomActivity.class));
    }

    private void joinRoom(){
        startActivity(new Intent(this, JoinRoomActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // We will start writing our code here.
    }
}
