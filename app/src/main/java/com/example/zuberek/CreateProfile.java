package com.example.zuberek;

import androidx.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class CreateProfile extends AppCompatActivity {

    EditText etName, etSurname, etPhone;

    ProgressBar progressBar;
    Button button;
    ImageView imageView;
    Uri imgUri;
    UploadTask uploadTask;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final int PICK_IMAGE=1;
    String currentUserId;
    AllUserMember allUserMember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        etName = findViewById(R.id.et_name_cp);
        etSurname = findViewById(R.id.et_surname_cp);
        etPhone = findViewById(R.id.et_phonenumber_cp);
        button = findViewById(R.id.btn_cp);
        imageView = findViewById(R.id.iv_cp);
        progressBar = findViewById(R.id.progressbar_cp);
        allUserMember = new AllUserMember();

        FirebaseUser user = auth.getCurrentUser();

        currentUserId = user.getUid();
        docRef = db.collection("Users").document(currentUserId);
        storageReference = FirebaseStorage.getInstance().getReference("Profile images");
        databaseReference = database.getReference("All users");

        button.setOnClickListener(view -> {
            uploadData();
        });

        imageView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if(requestCode == PICK_IMAGE || resultCode == RESULT_OK || data !=null || data.getData() != null){
                imgUri = data.getData();
                Picasso.get().load(imgUri).into(imageView);
            }
        }catch(Exception e){
            Toast.makeText(this, "Error "+e, Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadData(){
    String name = etName.getText().toString();
    String surname = etSurname.getText().toString();
    String phone = etPhone.getText().toString();

    if(!TextUtils.isEmpty(name) ||!TextUtils.isEmpty(surname) ||!TextUtils.isEmpty(phone) || imgUri !=null) {

        progressBar.setVisibility(View.VISIBLE);
        final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(imgUri));
        uploadTask = reference.putFile((imgUri));

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }

                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Uri downloadUri = task.getResult();

                Map<String , String> profile = new HashMap<>();
                profile.put("name", name);
                profile.put("surname", surname);
                profile.put("phone", phone);
                profile.put("url", downloadUri.toString());
                profile.put("privacy", "Public");

                allUserMember.setName(name);
                allUserMember.setSurname(surname);
                allUserMember.setPhone(phone);

                databaseReference.child(currentUserId).setValue(allUserMember);

                docRef.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(CreateProfile.this, "Profil utworzony", Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent(CreateProfile.this, MainActivity.class);
                        //startActivity(intent);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(CreateProfile.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }, 2000);
                    }
                })
;            }
        });
    }else{
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
    }
    }
}