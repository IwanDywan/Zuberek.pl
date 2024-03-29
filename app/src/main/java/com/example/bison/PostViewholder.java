package com.example.bison;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewholder extends RecyclerView.ViewHolder {

    ImageView iv_post;
    CircleImageView imageViewprofile;
    TextView tv_desc, tv_likes,tv_comment,tv_time,tv_nameprofile;
    public ImageButton likebtn;
    public ImageButton commentbtn;
    DatabaseReference likesref,commentref,blockref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int likescount,commentcount;
    CardView cardView;
    LinearLayout linearLayout;



    public PostViewholder(@NonNull View itemView) {
        super(itemView);
    }


    public void SetPost(FragmentActivity activity, String name, String url,String postUri,String time,String uid,String type,String desc)
    {
        imageViewprofile = itemView.findViewById(R.id.iv_profile_item);
        iv_post = itemView.findViewById(R.id.iv_post_item);
        tv_desc = itemView.findViewById(R.id.tv_desc_post);
        commentbtn = itemView.findViewById(R.id.commentbutton_posts);
        likebtn = itemView.findViewById(R.id.likebutton_posts);
        tv_likes = itemView.findViewById(R.id.tv_likes_post);
        tv_time = itemView.findViewById(R.id.tv_time_post);
        tv_nameprofile = itemView.findViewById(R.id.tv_name_post);
        cardView = itemView.findViewById(R.id.cv_post);
        linearLayout = itemView.findViewById(R.id.ll_post);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = Objects.requireNonNull(user).getUid();

        blockref = database.getReference("Block users").child(currentuid);

        blockref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(uid)){
                    cardView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ExoPlayer exoPlayer;
        PlayerView playerView = itemView.findViewById(R.id.exoplayer_item_post);

        if( type.equals("iv")) {
            Picasso.get().load(url).into(imageViewprofile);
            Picasso.get().load(postUri).into(iv_post);
            tv_desc.setText(desc);
            tv_time.setText(time);
            tv_nameprofile.setText(name);
            playerView.setVisibility(View.INVISIBLE);
        }
        else if( type.equals("vv")) {
            Picasso.get().load(url).into(imageViewprofile);
            iv_post.setVisibility(View.INVISIBLE);
            tv_desc.setText(desc);
            tv_time.setText(time);
            tv_nameprofile.setText(name);

            try {
                ExoPlayer simpleExoPlayer = new ExoPlayer.Builder(activity).build();
                playerView.setPlayer(simpleExoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(postUri);
                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(false);
            }
            catch ( Exception e) {
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void likeschecker(final String postkey) {
        likebtn = itemView.findViewById(R.id.likebutton_posts);

        likesref = database.getReference("post likes");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = Objects.requireNonNull(user).getUid();

        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postkey).hasChild(uid)) {
                    likebtn.setImageResource(R.drawable.ic_like);
                } else {
                    likebtn.setImageResource(R.drawable.ic_dislike);
                }
                likescount = (int) snapshot.child(postkey).getChildrenCount();
                tv_likes.setText(likescount + "likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void commentchecker(final String postkey) {
        tv_comment = itemView.findViewById(R.id.tv_comment_post);
        commentref = database.getReference("All Posts").child(postkey).child("Comments");

        commentref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                commentcount = (int) snapshot.getChildrenCount();
                tv_comment.setText(commentcount +" Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
