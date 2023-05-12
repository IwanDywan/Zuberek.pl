package com.example.zuberek;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class CatalogAdapter extends FirebaseRecyclerAdapter<CatalogModel, CatalogAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CatalogAdapter(@NonNull FirebaseRecyclerOptions<CatalogModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull CatalogModel model) {
        //holder.age.setText(model.getAge());
        holder.name.setText(model.getName());
        //holder.weight.setText(model.getWeight());

        Glide.with(holder.img.getContext()).load(model.getTurl()).
                placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop().
                error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.img);
    }

    @androidx.annotation.NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_item, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img;
        TextView age, name, weight;

        public myViewHolder(@NonNull View itemView){
            super(itemView);

           // age = itemView.findViewById(R.id.agetext);
            name =  itemView.findViewById(R.id.nametext);
            //weight = itemView.findViewById(R.id.weighttext);
            img = itemView.findViewById(R.id.img1);
        }
    }
}
