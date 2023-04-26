package com.example.zuberek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText username;
    private Button register;
    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    email = findViewById(R.id.editTextEmail);
    password = findViewById(R.id.editTextPassword);
    username = findViewById(R.id.editTextUsername);
    register = findViewById(R.id.buttonRegister);
    auth = FirebaseAuth.getInstance();

    register.setOnClickListener(view -> {
        String txtEmail = email.getText().toString();
        String txtPassword = password.getText().toString();
        String txtUsername = username.getText().toString();

        if(TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtUsername)){
            Toast.makeText(RegisterActivity.this, "Nie podano danych", Toast.LENGTH_SHORT).show();
        }else if (txtPassword.length()<6){
            Toast.makeText(RegisterActivity.this, "Hasło musi mieć przynajmniej 6 znaki", Toast.LENGTH_SHORT).show();
        }else{
            registerUser(txtEmail,txtPassword, txtUsername);
        }
    });
    }
    private void registerUser(String email, String password, String username) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
            if(task.isSuccessful()){
                Map<String, Object> userData = new HashMap<>();
                userData.put(KEY_USERNAME, username);
                userData.put(KEY_EMAIL, email);
                db.collection("Users").document(auth.getCurrentUser().getUid()).set(userData);
                Toast.makeText(RegisterActivity.this, "Zarejestrowano!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, StartActivity.class));
                finish();
            }else{
                Toast.makeText(RegisterActivity.this, "Rejestracja nieudana!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}