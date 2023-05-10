package com.example.zuberek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.annotation.Nullable;

public class PostActivity extends AppCompatActivity {

    ImageView imageView;
    EditText etDesc;
    VideoView videoView;
    String url , name;
    Button btnChoose, btnUpload;
    private Uri selectedUri;
    private static final int PICK_FILE=1;
    UploadTask uploadTask;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference db1, db2, db3;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    StorageReference storageReference;
    MediaController mediaController;
    String type;

    Postmember postmember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postmember = new Postmember();
        mediaController = new MediaController(this);
        imageView = findViewById(R.id.iv_post);
        videoView = findViewById(R.id.vv_post);
        btnChoose = findViewById(R.id.btn_choose_post);
        btnUpload = findViewById(R.id.btn_upload_post);
        etDesc = findViewById(R.id.et_desc_post);

        storageReference = FirebaseStorage.getInstance().getReference("User posts");
        FirebaseUser user = auth.getCurrentUser();
        String currentUid = user.getUid();

        db1 = database.getReference("All images").child(currentUid);
        db2 = database.getReference("All videos").child(currentUid);
        db3 = database.getReference("All posts");

        btnUpload.setOnClickListener(view -> {
            doPost();
            Log.i("my tag", "Button upload");
        });

        btnChoose.setOnClickListener(view -> {
            chooseImage();
        });
    }

    private void doPost() {
        FirebaseUser user = auth.getCurrentUser();
        String currentUid = user.getUid();

        String desc = etDesc.getText().toString();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveDate = currentDate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        final String saveTime = currentTime.format(ctime.getTime());

        String time = saveDate+":"+saveTime;

        if(TextUtils.isEmpty(desc) || selectedUri !=null)
        {
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt((selectedUri)));
            uploadTask = reference.putFile(selectedUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()) {
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    if(type.equals("iv")){
                        postmember.setName(name);
                        postmember.setTime(time);
                        postmember.setUrl(url);
                        postmember.setPostUri(downloadUri.toString());
                        postmember.setType("iv");
                        postmember.setUid(currentUid);
                        postmember.setDescription(desc);

                        //all images
                        String id = db1.push().getKey();
                        db1.child(id).setValue(postmember);
                        //all posts
                        String id1 = db3.push().getKey();
                        db3.child(id1).setValue(postmember);
                        Toast.makeText(this, "Post dodano", Toast.LENGTH_SHORT).show();

                    }else if(type.equals("vv")){
                        postmember.setName(name);
                        postmember.setTime(time);
                        postmember.setUrl(url);
                        postmember.setPostUri(downloadUri.toString());
                        postmember.setType("vv");
                        postmember.setUid(currentUid);
                        postmember.setDescription(desc);

                        //all images
                        String id2 = db2.push().getKey();
                        db2.child(id2).setValue(postmember);
                        //all posts
                        String id3 = db3.push().getKey();
                        db3.child(id3).setValue(postmember);

                        Toast.makeText(this, "Post dodano", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void chooseImage(){
        //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //intent.setType("image/* video/*");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/* video/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE || requestCode == RESULT_OK || data != null || data.getData() != null){
            selectedUri = data.getData();
            if(selectedUri.toString().contains("image")){
                Picasso.get().load(selectedUri).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                type = "iv";
            }else if(selectedUri.toString().contains("video")){
                videoView.setMediaController(mediaController);
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type = "vv";
            }else{
                Toast.makeText(this, "Nie wybrano żadnego pliku", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = auth.getCurrentUser();
        String currentUid = user.getUid();
        DocumentReference docRef = db.collection("Users").document(currentUid);

        docRef.get().addOnCompleteListener(task -> {
            if(task.getResult().exists())
            {
                name = task.getResult().getString("name");
                url = task.getResult().getString("url");
            }else{
                Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}