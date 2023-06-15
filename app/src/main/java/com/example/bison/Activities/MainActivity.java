package com.example.bison.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.bison.Fragments.Fragment1;
import com.example.bison.Fragments.Fragment2;
import com.example.bison.Fragments.Fragment4;
import com.example.bison.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid;

        if( user == null )
        {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            uid = user.getUid();

            try {

                SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs",MODE_PRIVATE);

                final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn",false);

                if (isDarkModeOn){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                }
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Token is missing", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        FirebaseDatabase.getInstance().getReference("Token").child(uid).child("token").setValue(token);

                    });

            BottomNavigationView bottomNavigationView =findViewById(R.id.bottom_nav);
            bottomNavigationView.setOnNavigationItemSelectedListener(onNav);

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new Fragment1()).commit();

        }


    }

    private final BottomNavigationView.OnNavigationItemSelectedListener onNav = item -> {

        Fragment selected = null;

        switch (item.getItemId())
        {
            case R.id.profile_bottom:
                selected = new Fragment1();
                break;
            case R.id.ask_bottom:
                selected = new Fragment2();
                break;
            case R.id.home_bottom:
                selected = new Fragment4();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, Objects.requireNonNull(selected)).commit();
        return true;
    };

    public void logout(View view) {
        auth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if( user == null )
        {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}