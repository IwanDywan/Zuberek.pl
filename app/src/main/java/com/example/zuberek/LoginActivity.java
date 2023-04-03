package com.example.zuberek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        login = findViewById(R.id.buttonLogin);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(view -> {
            String txtemail = email.getText().toString();
            String txtpassword = password.getText().toString();
            loginUser(txtemail, txtpassword);
        });
    }
    private void loginUser(String email, String password)
    {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
            if(task.isSuccessful())
            {
                Toast.makeText(LoginActivity.this, "Zalogowano!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }else{
                //Toast.makeText(LoginActivity.this, "Logowanie nieudane!", Toast.LENGTH_SHORT).show();
                Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}