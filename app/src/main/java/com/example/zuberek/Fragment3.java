package com.example.zuberek;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Fragment3 extends Fragment implements View.OnClickListener{

    Button button;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference, likeref;
    Boolean likechecker = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment3, container, false);
    }

    @Override
    public void onActivityCreated(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        button = getActivity().findViewById(R.id.createpost_f3);
        databaseReference = database.getReference("All posts");
        likeref = database.getReference("post likes");
        recyclerView = getActivity().findViewById(R.id.rv_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){

        switch(view.getId()){
            case R.id.createpost_f3:
                Intent intent = new Intent(getActivity(), PostActivity.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Postmember> options = new FirebaseRecyclerOptions.Builder<Postmember>()
                .setQuery(databaseReference, Postmember.class)
                .build();

        FirebaseRecyclerAdapter<Postmember, PostViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Postmember, PostViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Postmember model) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String currentUserid = user.getUid();
                        final String postkey = getRef(position).getKey();
                        holder.SetPost(getActivity(), model.getName(), model.getUrl(), model.getPostUri(),
                                model.getTime(), model.getType(), model.getDescription(), model.getUid());

                        holder.likesChecker(postkey);
                        holder.likebtn.setOnClickListener((view) ->{
                            likechecker = true;

                            likeref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(likechecker.equals(true)){
                                        if(snapshot.child(postkey).hasChild(currentUserid)){
                                            likeref.child(postkey).child(currentUserid).removeValue();
                                            Toast.makeText(getActivity(), "Usunieto z ulubionych", Toast.LENGTH_SHORT).show();
                                            likechecker = false;
                                        }else{
                                            likeref.child(postkey).child(currentUserid).setValue(true);
                                            likechecker = false;
                                            Toast.makeText(getActivity(), "Dodano do ulubionych", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        });
                    }

                    @NonNull
                    @Override
                    public PostViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent ,false);
                        return new PostViewHolder(view);
                    }
                };
                firebaseRecyclerAdapter.startListening();
                recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
