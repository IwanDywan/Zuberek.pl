package com.example.bison.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bison.Activities.AddListingActivity;
import com.example.bison.Activities.ProductDetailsActivity;
import com.example.bison.All_ProductMember;
import com.example.bison.R;
import com.example.bison.Viewholder_Product;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment2 extends Fragment implements View.OnClickListener{
    FloatingActionButton fb;
    CircleImageView imageView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    RecyclerView recyclerView;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = Objects.requireNonNull(user).getUid();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment2,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = Objects.requireNonNull(user).getUid();

        imageView = requireActivity().findViewById(R.id.iv_f2);
        //fb = requireActivity().findViewById(R.id.floatingActionButton);
        reference = db.collection("user").document(currentUserid);

        recyclerView = requireActivity().findViewById(R.id.rv_f2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        databaseReference = database.getReference("All Products");


        //fb.setOnClickListener(this);
        imageView.setOnClickListener(this);

        FirebaseRecyclerOptions<All_ProductMember> options =
                new FirebaseRecyclerOptions.Builder<All_ProductMember>()
                        .setQuery(databaseReference,All_ProductMember.class)
                        .build();

        FirebaseRecyclerAdapter<All_ProductMember, Viewholder_Product> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<All_ProductMember, Viewholder_Product>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Viewholder_Product holder, @SuppressLint("RecyclerView") int position, @NonNull All_ProductMember model) {

                        holder.setitem(getActivity(), model.getName() , model.getUrl() , model.getUserid(), model.getKey(),
                                model.getPrivacy(),model.getTime(),model.getProduct(), model.getProductImgUrl(), model.getLocation(),
                                model.getContact(), model.getPrice() , model.getDescription());

                        holder.v.setOnClickListener(view -> {
                            Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                            intent.putExtra("ProductKey",getRef(position).getKey());
                            startActivity(intent);

                        });

                    }

                    @NonNull
                    @Override
                    public Viewholder_Product onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.product_item,parent,false);

                        return new Viewholder_Product(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.iv_f2:
                break;
            /*case R.id.floatingActionButton:
                Intent intent = new Intent(getActivity(), AddListingActivity.class);
                startActivity(intent);
                break;*/
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        reference.get()
                .addOnCompleteListener((task) -> {
                   if( task.getResult().exists() )
                   {
                       String url = task.getResult().getString("url");

                       Picasso.get().load(url).into(imageView);
                   }
                   else
                   {
                       Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                   }
                });
    }
}
