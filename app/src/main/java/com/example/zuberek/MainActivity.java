package com.example.zuberek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(onNav);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new Fragment1()).commit();
    }

    private BottomNavigationView.OnItemSelectedListener onNav = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected = null;

            switch(item.getItemId())
            {
                case R.id.profile_bottom:
                    selected = new Fragment1();
                    break;

                case R.id.home_bottom:
                    selected = new Fragment2();
                    break;

                case R.id.post_bottom:
                    selected = new Fragment3();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selected).commit();
            return true;
        }
    };
}