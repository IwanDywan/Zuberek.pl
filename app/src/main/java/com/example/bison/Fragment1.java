package com.example.bison;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment1 extends Fragment implements View.OnClickListener{
    CircleImageView imageView;
    TextView nameEt, profEt, bioEt,emailEt,webEt,postTv,storyadd,followertv,newtv;
    Button btnsendmessage;
    ImageButton imageButtonEdit,imageButtonMenu;

    String userid;
    DocumentReference reference;
    FirebaseFirestore firestore;

    private static final int PICK_IMAGE = 1;
    FirebaseAuth mAuth;

    int postiv,post1,post2,newcount;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db1,db2,db3,ntRef;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("user").document(userid);

        db1 = database.getReference("followers").child(userid);
        db2 = database.getReference("All images").child(userid);
        db3 = database.getReference("All videos").child(userid);

        ntRef = database.getReference("notification").child(userid);

        imageView = getActivity().findViewById(R.id.iv_f1);
        nameEt = getActivity().findViewById(R.id.tv_name_f1);
        profEt = getActivity().findViewById(R.id.tv_prof_f1);
        bioEt = getActivity().findViewById(R.id.tv_bio_f1);
        emailEt = getActivity().findViewById(R.id.tv_email_f1);
        webEt = getActivity().findViewById(R.id.tv_web_f1);
        postTv = getActivity().findViewById(R.id.tv_post_f1);
        storyadd = getActivity().findViewById(R.id.tv_addstories_f1);
        newtv = getActivity().findViewById(R.id.tv_newf1);

        btnsendmessage = getActivity().findViewById(R.id.btn_sendmessage_f1);
        followertv = getActivity().findViewById(R.id.tv_friends_f1);

        imageButtonEdit = getActivity().findViewById(R.id.ib_edit_f1);
        imageButtonMenu = getActivity().findViewById(R.id.ib_menu_f1);
        postTv.setOnClickListener(this);

        FirebaseMessaging.getInstance().subscribeToTopic("all");


        imageButtonEdit.setOnClickListener(this);
        imageButtonMenu.setOnClickListener(this);
        btnsendmessage.setOnClickListener(this);
        imageView.setOnClickListener(this);
        webEt.setOnClickListener(this);
        storyadd.setOnClickListener(this);
        followertv.setOnClickListener(this);
        newtv.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();



//        checkIncoming();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.ib_edit_f1:
                Intent intent = new Intent(getActivity(), UpdateProfile.class);
                startActivity(intent);
                break;
            case R.id.ib_menu_f1:
                showBottomSheet();
                break;
            case R.id.iv_f1:
               Intent intent1 = new Intent(getActivity(),ImageActivity.class);
               startActivity(intent1);
                break;
            case R.id.tv_newf1:
                Intent intent3 = new Intent(getActivity(),NotificationActivity.class);
                startActivity(intent3);
                changeSeen();
                break;
            case R.id.tv_post_f1:
                Intent intent5 = new Intent(getActivity(),IndividualPost.class);
                startActivity(intent5);
                break;
            case R.id.tv_addstories_f1:
                Intent intentstory = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentstory.setType("image/*");
                startActivityForResult(intentstory,PICK_IMAGE);
                break;
            case R.id.tv_web_f1:
                try{
                    String url = webEt.getText().toString();
                    Intent intent2 = new Intent(Intent.ACTION_VIEW);
                    intent2.setData(Uri.parse(url));
                    startActivity(intent2);
                }catch( Exception e){
                    Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showBottomSheet() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_menu);

        ImageView logout,settings,delete;

        logout = dialog.findViewById(R.id.logout_profile);
        delete = dialog.findViewById(R.id.del_profile);
        settings = dialog.findViewById(R.id.settings_profile);

        logout.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Logout")
                    .setMessage("Are you sure to Logout")
                    .setPositiveButton("yes", (dialogInterface, i) -> {

                        mAuth.signOut();

                        FirebaseDatabase.getInstance().getReference("Token").child(userid).child("token").removeValue();
                        startActivity(new Intent(getActivity(),LoginActivity.class));

                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {

                    });
            builder.create();
            builder.show();

        });

        delete.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Delete Profile")
                    .setMessage("Are you sure to delete?")
                    .setPositiveButton("yes", (dialogInterface, i) -> {

                        //   StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl()

                        deleteImage();
                        reference.delete()

                                .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Profile deleted", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Profile delete failed", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {

                    });
            builder.create();
            builder.show();



        });

        settings.setOnClickListener(view -> startActivity(new Intent(getActivity(),SettingsActivity.class)));


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void deleteImage() {

        reference.get()
                .addOnCompleteListener(task -> {

                    if (task.getResult().exists()) {

                        String Url = task.getResult().getString("url");
                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(Url);
                        reference.delete()
                                .addOnSuccessListener(aVoid -> {
                                });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = ntRef.orderByChild("seen").equalTo("no");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    newcount = (int) snapshot.getChildrenCount();
                    newtv.setText(newcount +" New");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postiv = (int)snapshot.getChildrenCount();
                String total = Integer.toString(postiv);
                followertv.setText(total+" Followers ");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post1 = (int)snapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post2 = (int)snapshot.getChildrenCount();
                String total = Integer.toString(post1+post2);
                postTv.setText(total+" Posts");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if( user == null )
        {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().getFragmentManager().popBackStack();
        }
        else
        {
            String currentid = user.getUid();


            DocumentReference reference;
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            reference = firestore.collection("user").document(currentid);


            reference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if( task.getResult().exists() )
                            {

                                String nameResult = task.getResult().getString("name");
                                String bioResult = task.getResult().getString("bio");
                                String emailResult = task.getResult().getString("email");
                                String webResult = task.getResult().getString("web");
                                String url = task.getResult().getString("url");
                                String profResult = task.getResult().getString("prof");

                                Picasso.get().load(url).into(imageView);
                                nameEt.setText(nameResult);
                                bioEt.setText(bioResult);
                                emailEt.setText(emailResult);
                                webEt.setText(webResult);
                                profEt.setText(profResult);
                            }
                            else
                            {
                                Intent intent = new Intent(getActivity(),CreateProfile.class);
                                startActivity(intent);
                            }
                        }
                    });
        }

    }

    private void changeSeen(){

        Map<String,Object > profile = new HashMap<>();
        profile.put("seen","yes");

        ntRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().updateChildren(profile)
                            .addOnSuccessListener(aVoid -> {
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
