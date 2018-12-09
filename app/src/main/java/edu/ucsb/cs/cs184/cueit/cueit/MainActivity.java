package edu.ucsb.cs.cs184.cueit.cueit;

import android.os.Bundle;
import android.app.FragmentManager;
//import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.android.youtube.player.YouTubeBaseActivity;


public class MainActivity extends YouTubeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayFragment();
        FirebaseHelper.Initialize(this);
    }

    public void displayFragment() {
        MainMenuFragment mainMenuFragment = MainMenuFragment.newInstance();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mainMenuFragment, "mainMenu");

        fragmentTransaction.commit();

        //fragmentTransaction.add(R.id.fragment_container, mainMenuFragment
        //       ).addToBackStack(null).commit();
        // Update the Button text.
    }


}
