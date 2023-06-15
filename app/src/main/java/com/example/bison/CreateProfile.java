package com.example.bison;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bison.Activities.MainActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfile extends AppCompatActivity {

    CircleImageView imageView;

    EditText etName,etBio,etEmail;
    Button button;

    Uri imageUri;
    UploadTask uploadTask;

    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    private static final int PICK_IMAGE = 1;
    All_UserMember member;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        member = new All_UserMember();
        imageView = findViewById(R.id.iv_cp);
        etBio = findViewById(R.id.et_bio_cp);
        etEmail = findViewById(R.id.et_email_cp);
        etName = findViewById(R.id.et_name_cp);
        button = findViewById(R.id.btn_cp);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = Objects.requireNonNull(user).getUid();

        documentReference = db.collection("user").document(currentUserId);
        storageReference = FirebaseStorage.getInstance().getReference("Profile Images");
        databaseReference = database.getReference("ALl Users");

        imageView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,PICK_IMAGE);
        });

        button.setOnClickListener(view -> uploadData());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if( requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null || data.getData() != null ) {
                imageUri = Objects.requireNonNull(data).getData();
                Picasso.get().load(imageUri).into(imageView);
            }
        } catch ( Exception e ) {
            Toast.makeText(this,"Error "+e, Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExt( Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }


    private void uploadData() {

        String name = etName.getText().toString();
        String bio = etBio.getText().toString();
        String email = etEmail.getText().toString();

        if( !TextUtils.isEmpty(name) && !TextUtils.isEmpty(bio) && !TextUtils.isEmpty(email) && imageUri != null )
        {
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+ "."+getFileExt(imageUri));
            uploadTask = reference.putFile(imageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {

                if( !task.isSuccessful())
                {
                    throw Objects.requireNonNull(task.getException());
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {

                if( task.isSuccessful())
                {
                    Uri downloadUri = task.getResult();

                    Map<String,String > profile = new HashMap<>();
                    profile.put("name",name.toUpperCase());
                    profile.put("url",downloadUri.toString());
                    profile.put("email",email);
                    profile.put("bio",bio);
                    profile.put("uid",currentUserId);
                    profile.put("privacy","Public");

                    member.setName(name);
                    member.setUid(currentUserId);
                    member.setUrl(downloadUri.toString());

                    databaseReference.child(currentUserId).setValue(member);

                    documentReference.set(profile)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(CreateProfile.this,"Profile Created", Toast.LENGTH_SHORT).show();

                                Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                    Intent intent = new Intent(CreateProfile.this, MainActivity.class);
                                    startActivity(intent);
                                },2000);
                            });
                }

            });
        }
        else
        {
            Toast.makeText(this,"Please fill all Fields", Toast.LENGTH_SHORT).show();
        }
    }
}