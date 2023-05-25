package com.example.bison;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.Objects;

public class UpdateProfile extends AppCompatActivity {

    EditText etName,etBio, etEmail;
    Button button;

    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = Objects.requireNonNull(user).getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = Objects.requireNonNull(user).getUid();
        documentReference = db.collection("user").document(currentuid);

        etBio = findViewById(R.id.et_bio_up);
        etEmail = findViewById(R.id.et_email_up);
        etName = findViewById(R.id.et_name_up);
        button = findViewById(R.id.btn_up);

        button.setOnClickListener(view -> updateProfile());

    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(task -> {


                    if( task.getResult().exists() ) {
                        String nameResult = task.getResult().getString("name");
                        String bioResult = task.getResult().getString("bio");
                        String emailResult = task.getResult().getString("email");

                        etName.setText(nameResult);
                        etBio.setText(bioResult);
                        etEmail.setText(emailResult);
                    }
                    else {
                        Toast.makeText(UpdateProfile.this,"No Profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProfile() {

        String name = etName.getText().toString();
        String bio = etBio.getText().toString();
        String email = etEmail.getText().toString();

        final DocumentReference sDoc = db.collection("user").document(currentuid);

        db.runTransaction((Transaction.Function<Void>) transaction -> {

            transaction.update(sDoc, "name",name);
            transaction.update(sDoc, "email",email);
            transaction.update(sDoc, "bio",bio);

            return null;
        }).addOnSuccessListener(aVoid -> Toast.makeText(UpdateProfile.this, "Updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(UpdateProfile.this, "Failed", Toast.LENGTH_SHORT).show());
    }
}