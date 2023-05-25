package com.example.bison;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    EditText emailET,passwordET;
    Button signup_btn, login_btn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    TextView forgotpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        forgotpass = findViewById(R.id.tv_forgot_pass);
        emailET = findViewById(R.id.login_email_ET);
        passwordET = findViewById(R.id.login_password_ET);
        login_btn = findViewById(R.id.login_BTN);
        signup_btn = findViewById(R.id.login_to_signup_BTN);
        progressBar = findViewById(R.id.progressbar_login);
        mAuth = FirebaseAuth.getInstance();

        signup_btn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        login_btn.setOnClickListener(view -> {
            String email = emailET.getText().toString();
            String pass = passwordET.getText().toString();

            if( !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) )
            {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                        sendtoMain();
                    }
                    else
                    {
                        String error = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error :"+error, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Wrong Login Credentials", Toast.LENGTH_SHORT).show());

            }
            else
            {
                Toast.makeText(LoginActivity.this,"Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        forgotpass.setOnClickListener(view -> {

            String email = emailET.getText().toString();

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Reset Password")
                    .setMessage("Are you sure to reset password")
                    .setPositiveButton("yes", (dialogInterface, i) -> mAuth.sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> Toast.makeText(LoginActivity.this, "Reset Link sent", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error"+e, Toast.LENGTH_SHORT).show()))
                    .setNegativeButton("No", (dialogInterface, i) -> {

                    });
            builder.create();
            builder.show();

        });


    }

    private void sendtoMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if( user != null )
        {
            Intent intent = new Intent(LoginActivity.this, Splashscreen.class);
            startActivity(intent);
            finish();
        }
    }
}