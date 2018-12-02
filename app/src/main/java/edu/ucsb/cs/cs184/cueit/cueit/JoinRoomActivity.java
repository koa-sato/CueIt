package edu.ucsb.cs.cs184.cueit.cueit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by deni on 11/30/18.
 */

public class JoinRoomActivity extends AppCompatActivity {
    TextView t;
    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        t = findViewById(R.id.room_id_edit);
        b = findViewById(R.id.join_room);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateEntryCode();
            }
        });
    }

    // Check if code is valid by checking firebase for existing room code
    public void validateEntryCode(){
        String code = t.getText().toString();
        boolean isValid = false;


        //TODO check Firebase for this entry code, set isValid to true or false


        if(isValid){
            startActivity(new Intent(this, JoinRoomActivity.class));
        }else{
            //no such code found, create toast
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No such group",
                    Toast.LENGTH_SHORT);

            toast.show();
        }

    }
}
