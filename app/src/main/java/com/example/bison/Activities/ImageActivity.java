package com.example.bison.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bison.R;
import com.example.bison.UpdatePhoto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ImageActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    Button btnEdit,btnDel;
    DocumentReference reference;
    String url;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = Objects.requireNonNull(user).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        btnDel = findViewById(R.id.btn_delete_iv);
        btnEdit = findViewById(R.id.btn_edit_iv);
        imageView = findViewById(R.id.iv_expand);
        textView = findViewById(R.id.tv_name_image);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentid = Objects.requireNonNull(user).getUid();

        reference = db.collection("user").document(currentid);

        btnEdit.setOnClickListener(view -> {
            Intent intent = new Intent(ImageActivity.this, UpdatePhoto.class);
            startActivity(intent);
        });

        btnDel.setOnClickListener(view -> {
            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            reference.delete().addOnSuccessListener(aVoid -> Toast.makeText(ImageActivity.this, "deleted", Toast.LENGTH_SHORT).show());
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.get().addOnCompleteListener(task -> {

            if( task.getResult().exists() )
            {
                String name = task.getResult().getString("name");
                url = task.getResult().getString("url");

                Picasso.get().load(url).into(imageView);
                textView.setText(name);
            }
            else
            {
                Toast.makeText(ImageActivity.this, "No Profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}