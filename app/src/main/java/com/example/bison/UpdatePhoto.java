package com.example.bison;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdatePhoto extends AppCompatActivity {

    CircleImageView imageView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;
    UploadTask uploadTask;
    ProgressBar progressBar;

    String currentuid;
    Button button,buttonch;
    private  final static int  PICK_IMAGE = 1;

    Uri imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_photo);

        imageView = findViewById(R.id.iv_updatephoto);
        button = findViewById(R.id.btn_updatephoto);
        buttonch = findViewById(R.id.btn_choosephoto);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = Objects.requireNonNull(user).getUid();


        storageReference = FirebaseStorage.getInstance().getReference("Profile images");

        buttonch.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,PICK_IMAGE);
        });

        button.setOnClickListener(view -> updateImage());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null || data.getData() != null) {
                imageuri = Objects.requireNonNull(data).getData();
                Picasso.get().load(imageuri).into(imageView);
            }
        }catch (Exception e){
            Toast.makeText(this, "Error"+e, Toast.LENGTH_SHORT).show();
        }


    }

    private String getFileExt( Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    private void updateImage() {

        if( imageuri != null )
        {
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+ "."+getFileExt(imageuri));
            uploadTask = reference.putFile(imageuri);

            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()){
                    throw Objects.requireNonNull(task.getException());
                }

                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {

                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();

                    final DocumentReference sDoc = db.collection("user").document(currentuid);
                    db.runTransaction((Transaction.Function<Void>) transaction -> {
                        DocumentSnapshot snapshot = transaction.get(sDoc);
                        transaction.update(sDoc, "url",downloadUri );

                        // Success
                        return null;
                    }).addOnSuccessListener(aVoid -> {
                        Toast.makeText(UpdatePhoto.this, "updated", Toast.LENGTH_SHORT).show();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference db1,db2;



                        Map<String,Object > profile = new HashMap<>();
                        profile.put("url",downloadUri.toString());


                        db1 = database.getReference("All posts");
                        Query query = db1.orderByChild("uid").equalTo(currentuid);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    dataSnapshot.getRef().updateChildren(profile)
                                            .addOnSuccessListener(aVoid1 -> Toast.makeText(UpdatePhoto.this, "done", Toast.LENGTH_SHORT).show());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        db2 = database.getReference("All Products");
                        Query query1 = db2.orderByChild("uid").equalTo(currentuid);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    dataSnapshot.getRef().updateChildren(profile)
                                            .addOnSuccessListener(aVoid12 -> Toast.makeText(UpdatePhoto.this, "done", Toast.LENGTH_SHORT).show());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    })
                            .addOnFailureListener(e -> Toast.makeText(UpdatePhoto.this, "failed", Toast.LENGTH_SHORT).show());
                }
            });

            uploadTask.addOnProgressListener(snapshot -> {

                double progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                button.setText(progress +" % Done");
            });
        }
        else
        {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    public void chooseimage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }
}