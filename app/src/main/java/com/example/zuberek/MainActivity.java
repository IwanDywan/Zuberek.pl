package com.example.zuberek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button logout;
    private Button delete;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference usersRef = db.collection("Users");

    private FirebaseAuth auth;

    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout = findViewById(R.id.buttonLogout);
        delete = findViewById(R.id.buttonDeleteAcc);
        auth = FirebaseAuth.getInstance();

        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            Toast.makeText(MainActivity.this, "Wylogowano!", Toast.LENGTH_SHORT).show();
        });

        delete.setOnClickListener(view -> {
            String usersUid = auth.getCurrentUser().getUid();
            auth.getCurrentUser().delete().addOnCompleteListener(MainActivity.this, task -> {
                if(task.isSuccessful())
                {
                    usersRef.document(usersUid).delete();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, StartActivity.class));
                    Toast.makeText(MainActivity.this, "Usunięto konto!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Usunięcie konta nieudane!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}