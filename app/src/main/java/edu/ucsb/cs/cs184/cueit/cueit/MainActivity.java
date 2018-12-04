package edu.ucsb.cs.cs184.cueit.cueit;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayFragment();
        FirebaseHelper.Initialize(this);
    }

    public void displayFragment() {
        MainMenuFragment mainMenuFragment = MainMenuFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        fragmentTransaction.add(R.id.fragment_container, mainMenuFragment
                ).addToBackStack(null).commit();
        // Update the Button text.
    }


}
