package com.example.zuberek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddBisonActivity extends AppCompatActivity {

    EditText age, name, turl, weight;
    Button btnAddBison, btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bison);

        age = findViewById(R.id.txtAge);
        name = findViewById(R.id.txtName);
        turl = findViewById(R.id.txtImageUrl);
        weight = findViewById(R.id.txtWeight);

        btnAddBison = findViewById(R.id.btnAddBison);
        btnReturn = findViewById(R.id.btnReturn);

        btnAddBison.setOnClickListener(view -> {
            insertData();
            clearAll();
        });

        btnReturn.setOnClickListener(view -> {
            finish();
        });
    }

    private void insertData() {
        Map<String, Object> map = new HashMap<>();
        map.put("age", age.getText().toString());
        map.put("name", name.getText().toString());
        map.put("turl", turl.getText().toString());
        map.put("weight", weight.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("zubry").push()
                .setValue(map).addOnSuccessListener(unused -> {
                    Toast.makeText(AddBisonActivity.this, "Dodano żubra", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddBisonActivity.this, "Nie udało się dodać żubra", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearAll(){
        age.setText("");
        name.setText("");
        turl.setText("");
        weight.setText("");
    }
}
