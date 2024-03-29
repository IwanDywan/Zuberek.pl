package com.example.bison.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bison.Activities.CommentsActivity;
import com.example.bison.Activities.PostActivity;
import com.example.bison.All_UserMember;
import com.example.bison.NewMember;
import com.example.bison.PostViewholder;
import com.example.bison.Postmember;
import com.example.bison.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Fragment4 extends Fragment implements View.OnClickListener{
    ImageButton button;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference,likeref,storyRef,likelist,referenceDel, ntref;
    Boolean likechecker = false;
    DatabaseReference db1, db2, db3;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = Objects.requireNonNull(user).getUid();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    NewMember newMember;
    LinearLayoutManager linearLayoutManager;

    String name_result, url_result, uid_result;
    All_UserMember userMember;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment4,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button = view.findViewById(R.id.createpost_f4);

        reference = database.getReference("All posts");
        likeref = database.getReference("post likes");
        storyRef = database.getReference("All story");
        referenceDel = database.getReference("story");

        recyclerView = view.findViewById(R.id.rv_posts);
        recyclerView.setHasFixedSize(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = Objects.requireNonNull(user).getUid();

        documentReference = db.collection("user").document(currentuid);

        newMember = new NewMember();

        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All posts");
        db3.keepSynced(true);

        linearLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        button.setOnClickListener(this);

        userMember = new All_UserMember();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.createpost_f4) {
            showBottomsheet();
        }
    }

    private void showBottomsheet() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.f4_bottomsheet);
        TextView tvcp = dialog.findViewById(R.id.tv_cpf4);

        tvcp.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), PostActivity.class);
            startActivity(intent);
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(task -> {
                    if( task.getResult().exists() ) {
                        name_result = task.getResult().getString("name");
                        url_result = task.getResult().getString("url");
                        uid_result = task.getResult().getString("uid");
                    }
                });

        FirebaseRecyclerOptions<Postmember> options =
                new FirebaseRecyclerOptions.Builder<Postmember>()
                        .setQuery(reference,Postmember.class)
                        .build();

        FirebaseRecyclerAdapter<Postmember, PostViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Postmember, PostViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PostViewholder holder, @SuppressLint("RecyclerView") int position, @NonNull final Postmember model) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String currentUserid = Objects.requireNonNull(user).getUid();

                        final String postkey = getRef(position).getKey();
                        holder.SetPost(getActivity(), model.getName(), model.getUrl(), model.getPostUri(),model.getTime(),model.getUid(),model.getType(), model.getDesc());

                        final String url = getItem(position).getPostUri();
                        final String name = getItem(position).getName();
                        final String userid = getItem(position).getUid();

                        holder.likeschecker(postkey);
                        holder.commentchecker(postkey);

                        holder.likebtn.setOnClickListener(view -> {

                            ntref = database.getReference("notification").child(userid);

                            likechecker = true;

                            likeref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (likechecker.equals(true)) {
                                        if (snapshot.child(Objects.requireNonNull(postkey)).hasChild(currentUserid)) {
                                            likeref.child(postkey).child(currentUserid).removeValue();
                                            likelist = database.getReference("like list").child(postkey).child(currentUserid);
                                            likelist.removeValue();

                                            ntref.child(currentUserid + "l").removeValue();
                                        } else {
                                            likeref.child(postkey).child(currentUserid).setValue(true);
                                            likelist = database.getReference("like list").child(postkey);
                                            userMember.setName(name_result);
                                            userMember.setUid(currentUserid);
                                            userMember.setUrl(url_result);
                                            likelist.child(currentUserid).setValue(userMember);

                                            newMember.setName(name_result);
                                            newMember.setUid(currentUserid);
                                            newMember.setUrl(url_result);
                                            newMember.setSeen("no");
                                            newMember.setText("Liked Your Post ");

                                            ntref.child(currentUserid + "l").setValue(newMember);
                                        }
                                        likechecker = false;
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        });

                        holder.commentbtn.setOnClickListener(view -> {
                            Intent intent = new Intent(getActivity(), CommentsActivity.class);
                            intent.putExtra("postkey", postkey);
                            intent.putExtra("name", name);
                            intent.putExtra("url", url);
                            intent.putExtra("uid", userid);
                            startActivity(intent);
                        });
                    }

                    @NonNull
                    @Override
                    public PostViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_layout,parent,false);

                        return new PostViewholder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
}