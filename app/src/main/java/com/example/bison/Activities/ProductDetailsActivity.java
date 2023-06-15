package com.example.bison.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bison.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDetailsActivity extends AppCompatActivity {

    ImageView iv_product;
    CircleImageView ib_profile;

    TextView tv_head,tv_location, tv_contact, tv_description, tv_price,tv_product_category;

    Button button;

    DatabaseReference ref;

    String name,userid,url;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = Objects.requireNonNull(user).getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        iv_product = findViewById(R.id.iv_product_product);

        tv_head = findViewById(R.id.tv_head_product);
        tv_product_category = findViewById(R.id.tv_productCategory_product);
        tv_location = findViewById(R.id.tv_location_product);
        tv_description = findViewById(R.id.tv_description_product);
        tv_price = findViewById(R.id.tv_price_product);

        button = findViewById(R.id.btn_product);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(ProductDetailsActivity.this, AdoptActivity.class);
            startActivity(intent);
        });

        ref = FirebaseDatabase.getInstance().getReference().child("All Products");

        String ProductKey = getIntent().getStringExtra("ProductKey");

        ref.child(ProductKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if( snapshot.exists() )
                {
                    name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    url = Objects.requireNonNull(snapshot.child("url").getValue()).toString();
                    userid = Objects.requireNonNull(snapshot.child("userid").getValue()).toString();
                    String product = Objects.requireNonNull(snapshot.child("product").getValue()).toString();
                    String category = Objects.requireNonNull(snapshot.child("category").getValue()).toString();
                    String productImgUrl = Objects.requireNonNull(snapshot.child("productImgUrl").getValue()).toString();
                    String location = Objects.requireNonNull(snapshot.child("location").getValue()).toString();
                    String price = Objects.requireNonNull(snapshot.child("price").getValue()).toString();
                    String description = Objects.requireNonNull(snapshot.child("description").getValue()).toString();

                    Picasso.get().load(productImgUrl).into(iv_product);

                    tv_head.setText(product);
                    tv_product_category.setText(category);
                    tv_location.setText(location);
                    tv_description.setText(description);
                    tv_price.setText(price);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}