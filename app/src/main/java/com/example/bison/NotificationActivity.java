package com.example.bison;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ntRef;
    String userid;
    LinearLayoutManager linearLayoutManager;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.rv_new);
        ntRef = database.getReference("notification").child(userid);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        ntRef.keepSynced(true);
        recyclerView.setLayoutManager(linearLayoutManager);

//        checkIncoming();
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<NewMember> options1 =
                new FirebaseRecyclerOptions.Builder<NewMember>()
                        .setQuery(ntRef, NewMember.class)
                        .build();

        FirebaseRecyclerAdapter<NewMember, NewViewHolder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<NewMember, NewViewHolder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull NewViewHolder holder, int position, @NonNull NewMember model) {

                    }

                    @NonNull
                    @Override
                    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.new_layout, parent, false);

                        return new NewViewHolder(view);
                    }
                };

        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);


    }
}