package edu.ucsb.cs.cs184.cueit.cueit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CreateRoomFragment extends Fragment {

    EditText code;
    Button codeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_room, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        code = view.findViewById(R.id.code_edittext);
        codeButton = view.findViewById(R.id.code_button);

        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomCode = code.getText().toString();
                FirebaseHelper.checkRoom(roomCode, new CheckRoomListener() {
                    @Override
                    public void onResponse(boolean roomExists) {
                        if (!roomExists) {
                            String roomCode = code.getText().toString();
                            FirebaseHelper.createRoom(roomCode);
                            startRoomFragment(roomCode);
                        }
                    }
                });

            }
        });
    }

    public void startRoomFragment(String code) {
        RoomFragment newFragment = new RoomFragment();
        Bundle args = new Bundle();
        newFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        FirebaseHelper.Room room = new FirebaseHelper.Room(code);

        transaction.commit();
    }
}
