package com.example.bison.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bison.CreateProfile;
import com.example.bison.R;

public class AdoptActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    String blik;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt);

        editText = findViewById(R.id.blik_et);
        button = findViewById(R.id.btn_pay);

        button.setOnClickListener(view -> {
            blik = editText.getText().toString();
            if(TextUtils.isEmpty(blik) || blik.length() < 6){
                Toast.makeText(AdoptActivity.this, "Incorrect code!", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(AdoptActivity.this, "Thank you for adoption!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdoptActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}