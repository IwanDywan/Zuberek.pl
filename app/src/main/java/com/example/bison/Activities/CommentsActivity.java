package com.example.bison.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bison.CommentsMember;
import com.example.bison.CommentsViewholder;
import com.example.bison.FcmNotificationsSender;
import com.example.bison.NewMember;
import com.example.bison.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    CircleImageView usernameImageview;
//    ImageView usernameImageview;
    TextView usernameTextview;
    Button commentsBtn;
    EditText commentsEdittext;
    String url, name, post_key, userid, bundleuid;
    DatabaseReference Commentref, userCommentref, likesref, ntref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String name_result, age_result, Url, uid, bio_result, web_result, email_result, usertoken;
    RecyclerView recyclerView;
    Boolean likeChecker = false;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = Objects.requireNonNull(user).getUid();


    NewMember newMember;
    CommentsMember commentsMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentsMember = new CommentsMember();

        newMember = new NewMember();
        recyclerView = findViewById(R.id.recycler_view_comments);


//        checkIncoming();
        recyclerView.setHasFixedSize(true);
        //   MediaController mediaController;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentsBtn = findViewById(R.id.btn_comments);
        usernameImageview = findViewById(R.id.imageviewUser_comment);
        usernameTextview = findViewById(R.id.name_comments_tv);
        commentsEdittext = findViewById(R.id.et_comments);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            url = extras.getString("url");
            name = extras.getString("name");
            post_key = extras.getString("postkey");
            bundleuid = extras.getString("uid");
        }


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = Objects.requireNonNull(user).getUid();
        Commentref = database.getReference("All Posts").child(post_key).child("Comments");

        likesref = database.getReference("comment likes");
        userCommentref = database.getReference("User Posts").child(userid);

        ntref = database.getReference("notification").child(bundleuid);

        commentsBtn.setOnClickListener(view -> comment());

    }

    @Override
    protected void onStart() {
        super.onStart();

        Picasso.get().load(url).into(usernameImageview);
        usernameTextview.setText(name);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(userid);

        documentReference.get().addOnCompleteListener(task -> {
                    if (task.getResult().exists()) {
                        name_result = task.getResult().getString("name");
                        age_result = task.getResult().getString("age");
                        bio_result = task.getResult().getString("bio");
                        email_result = task.getResult().getString("email");
                        web_result = task.getResult().getString("website");
                        Url = task.getResult().getString("url");
                        uid = task.getResult().getString("uid");
                    }
                });

        FirebaseRecyclerOptions<CommentsMember> options =
                new FirebaseRecyclerOptions.Builder<CommentsMember>()
                        .setQuery(Commentref, CommentsMember.class)
                        .build();

        FirebaseRecyclerAdapter<CommentsMember, CommentsViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<CommentsMember, CommentsViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CommentsViewholder holder, int position, @NonNull CommentsMember model) {


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserId = Objects.requireNonNull(user).getUid();
                        final String postkey = getRef(position).getKey();
                        String time = getItem(position).getTime();

                        holder.setComment(getApplication(), model.getComment(), model.getTime(), model.getUrl(), model.getUsername(), model.getUid());

                        holder.LikeChecker(postkey);

                        holder.delete.setOnClickListener(view -> {
                            Query query = Commentref.orderByChild("time").equalTo(time);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                        dataSnapshot1.getRef().removeValue();

                                        Toast.makeText(CommentsActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        });
                        holder.likebutton.setOnClickListener(view -> {

                            likeChecker = true;

                            likesref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (likeChecker.equals(true)) {
                                        if (snapshot.child(Objects.requireNonNull(postkey)).hasChild(currentUserId)) {
                                            likesref.child(postkey).child(currentUserId).removeValue();
                                        } else {
                                            likesref.child(postkey).child(currentUserId).setValue(true);
                                        }
                                        likeChecker = false;
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
                    public CommentsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item, parent, false);

                        return new CommentsViewholder(view);
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    private void comment() {

        Calendar callfordate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String savedate = currentdate.format(callfordate.getTime());


        Calendar callfortime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
        final String savetime = currenttime.format(callfortime.getTime());

        String time = savedate + ":" + savetime;
        String comment = commentsEdittext.getText().toString();
        if (comment != null) {

            commentsMember.setComment(comment);
            commentsMember.setUsername(name_result);
            commentsMember.setUid(uid);
            commentsMember.setTime(time);
            commentsMember.setUrl(Url);

            String pushkey = Commentref.push().getKey();
            Commentref.child(Objects.requireNonNull(pushkey)).setValue(commentsMember);

            commentsEdittext.setText("");


            newMember.setName(name_result);
            newMember.setUid(userid);
            newMember.setUrl(Url);
            newMember.setSeen("no");
            newMember.setText("Commented on your post: " + comment);

            String key = ntref.push().getKey();
            ntref.child(Objects.requireNonNull(key)).setValue(newMember);
            sendNotification(bundleuid, name_result, comment);

            Toast.makeText(this, "Commented", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Please write comment", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendNotification(String bundleuid, String name_result, String comment) {

        FirebaseDatabase.getInstance().getReference().child(bundleuid).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        usertoken = snapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            FcmNotificationsSender notificationsSender =
                    new FcmNotificationsSender(usertoken, "Social Media", name_result + " Commented on your post: " + comment,
                            getApplicationContext(), CommentsActivity.this);

            notificationsSender.SendNotifications();

        }, 3000);
    }
}