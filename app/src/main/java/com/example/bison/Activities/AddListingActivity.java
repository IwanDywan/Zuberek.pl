package com.example.bison.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bison.All_ProductMember;
import com.example.bison.R;
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
import java.util.Objects;

public class AddListingActivity extends AppCompatActivity {

    EditText etProduct,etCategory,etLocation,etPrice,etDescription;
    ImageView productImage;
    Button button;
    Uri imageUri;
    UploadTask uploadTask;

    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AllProducts, UserProducts;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    private static final int PICK_IMAGE = 1;
    String currentUserid;

    All_ProductMember member;

    String name,url,privacy,uid;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);


        member = new All_ProductMember();
        etProduct = findViewById(R.id.et_product_list);
        etCategory = findViewById(R.id.et_product_category);
        etLocation = findViewById(R.id.et_location_list);
        etPrice = findViewById(R.id.et_price_list);
        etDescription = findViewById(R.id.et_description_list);
        productImage = findViewById(R.id.iv_list);
        button = findViewById(R.id.btn_list);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserid = Objects.requireNonNull(user).getUid();

        documentReference = db.collection("user").document(currentUserid);

        storageReference = FirebaseStorage.getInstance().getReference("Product Images");

        AllProducts = database.getReference("All Products");
        UserProducts = database.getReference("User Products").child(currentUserid);

        productImage.setOnClickListener(view -> {
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
            if( requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null || data.getData() != null )
            {
                imageUri = Objects.requireNonNull(data).getData();
                Picasso.get().load(imageUri).into(productImage);
            }
        }
        catch ( Exception e )
        {
            Toast.makeText(this,"Error "+e, Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExt( Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }


    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(task -> {


                    if( task.getResult().exists() )
                    {
                        name = task.getResult().getString("name");
                        url = task.getResult().getString("url");
                        privacy = task.getResult().getString("privacy");
                        uid = task.getResult().getString("uid");
                    }
                    else
                    {
                        Toast.makeText(AddListingActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void uploadData() {
        String product = etProduct.getText().toString();
        String category = etCategory.getText().toString();
        String location = etLocation.getText().toString();
        String price = etPrice.getText().toString();
        String description = etDescription.getText().toString();


        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
        final String savetime = currenttime.format(ctime.getTime());

        String time = savedate +":"+ savetime;


        if( !TextUtils.isEmpty(product) && !TextUtils.isEmpty(category) && !TextUtils.isEmpty(location)&& !TextUtils.isEmpty(price)
                && !TextUtils.isEmpty(description) && imageUri != null )
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

                    member.setName(name);
                    member.setUrl(url);
                    member.setUserid(uid);
                    member.setPrivacy(privacy);
                    member.setTime(time);

                    member.setProduct(product);
                    member.setCategory(category);
                    member.setLocation(location);
                    member.setPrice(price);
                    member.setDescription(description);
                    member.setProductImgUrl(downloadUri.toString());


                    String id = UserProducts.push().getKey();
                    UserProducts.child(Objects.requireNonNull(id)).setValue(member);

                    String child = AllProducts.push().getKey();
                    member.setKey(id);
                    AllProducts.child(Objects.requireNonNull(child)).setValue(member);
                    Toast.makeText(AddListingActivity.this,"submitted", Toast.LENGTH_SHORT).show();
                }

            });
        }
        else
        {
            Toast.makeText(AddListingActivity.this,"Please fill all Fields", Toast.LENGTH_SHORT).show();
        }
    }
}