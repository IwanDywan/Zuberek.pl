package com.example.zuberek;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;

public class PostViewHolder extends RecyclerView.ViewHolder {

    ImageView imageViewProfile, iv_post;
    TextView tv_name, tv_desc, tv_likes, tv_comment, tv_time, tv_nameprofile;
    ImageButton likebtn, menuoptions, commentbtn;
    DatabaseReference likesref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    int likescount;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void SetPost(FragmentActivity activity, String name, String url, String postUri, String time, String uid,String type,String description){
        imageViewProfile = itemView.findViewById(R.id.ivprofile_item);
        iv_post = itemView.findViewById(R.id.iv_post_item);
        //tv_comment = itemView.findViewById(R.id.commentbutton_post);
        tv_desc = itemView.findViewById(R.id.tv_desc_post);
        commentbtn = itemView.findViewById(R.id.commentbutton_post);
        likebtn = itemView.findViewById(R.id.likebutton_post);
        tv_likes = itemView.findViewById(R.id.tv_likes_post);
        menuoptions = itemView.findViewById(R.id.morebutton_post);
        tv_time = itemView.findViewById(R.id.tv_time_post);
        tv_nameprofile = itemView.findViewById(R.id.tv_name_post);

        PlayerView playerView = itemView.findViewById(R.id.exoplayer_item_post);

        if(type.equals("iv")){
            Picasso.get().load(url).into(imageViewProfile);
            Picasso.get().load(postUri).into(iv_post);
            tv_desc.setText(description);
            tv_time.setText(time);
            tv_nameprofile.setText(name);
            playerView.setVisibility(View.INVISIBLE);
        }else if(type.equals("vv")){
            iv_post.setVisibility(View.INVISIBLE);
            tv_desc.setText(description);
            tv_time.setText(time);
            tv_nameprofile.setText(name);
            Picasso.get().load(url).into(imageViewProfile);

            try {
                SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(activity).build();
                playerView.setPlayer(simpleExoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(postUri);
                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(false);
            }catch(Exception e){
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void likesChecker(final String postkey)
    {
        likebtn = itemView.findViewById(R.id.likebutton_post);

        likesref = database.getReference("post likes");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postkey).hasChild(uid)){
                    likebtn.setImageResource(R.drawable.like);
                    likescount = (int) snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likescount)+"likes");
                }else{
                    likebtn.setImageResource(R.drawable.dislike);
                    likescount = (int) snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likescount)+"likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

