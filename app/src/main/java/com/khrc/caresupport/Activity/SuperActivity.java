package com.khrc.caresupport.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.khrc.caresupport.R;

public class SuperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super);

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set default fragment to Pregnant Woman Login
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new PregnantFragmentWomanLogin()).commit();

        // Set up BottomNavigationView item selection listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.pregnantWomanTab:
                        selectedFragment = new PregnantFragmentWomanLogin();
                        break;
                    case R.id.healthcareProviderTab:
                        selectedFragment = new HealthcareFragmentProviderLogin();
                        break;
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
                }
                return true;
            }
        });
    }
}