package com.example.zuberek;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

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
        holder.age.setText(model.getAge());
        holder.name.setText(model.getName());
        holder.weight.setText(model.getWeight());

        Glide.with(holder.img.getContext())
                .load(model.getTurl())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop()
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.img);

        holder.btnEdit.setOnClickListener(v -> {
            final DialogPlus dialogPlus = DialogPlus.newDialog(holder.img.getContext())
                    .setContentHolder(new ViewHolder(R.layout.update_popup))
                    .setExpanded(true, 1200)
                    .create();

            //dialogPlus.show();
            View view = dialogPlus.getHolderView();

            EditText name = view.findViewById(R.id.txtName);
            EditText age = view.findViewById(R.id.txtAge);
            EditText weight = view.findViewById(R.id.txtWeight);
            EditText turl = view.findViewById(R.id.txtImageUrl);

            Button btnUpdate =(Button)view.findViewById(R.id.btnUpdate);

            name.setText(model.getName());
            age.setText(model.getAge());
            weight.setText(model.getWeight());
            turl.setText(model.getTurl());

            dialogPlus.show();

            btnUpdate.setOnClickListener(view1 -> {
                Map<String,Object> map = new HashMap<>();
                map.put("name", name.getText().toString());
                map.put("age", age.getText().toString());
                map.put("weight", weight.getText().toString());
                map.put("turl", turl.getText().toString());

                FirebaseDatabase.getInstance().getReference().child("zubry")
                        .child(getRef(holder.getAbsoluteAdapterPosition()).getKey()).updateChildren(map)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(holder.name.getContext(), "Zapisano zmiany", Toast.LENGTH_SHORT).show();
                            dialogPlus.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.name.getContext(), "Błąd podczas zapisywania", Toast.LENGTH_SHORT).show();
                            dialogPlus.dismiss();
                        });
            });
        });

        holder.btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.name.getContext());
            builder.setTitle("Jesteś pewien?");
            builder.setMessage("Nie można odzyskać usuniętych danych!");

            builder.setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FirebaseDatabase.getInstance().getReference().child("zubry")
                            .child(getRef(holder.getAbsoluteAdapterPosition()).getKey()).removeValue();
                }
            });

            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(holder.name.getContext(), "Anulowano", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_item, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img;
        TextView age, name, weight;
        Button btnEdit, btnDelete;

        public myViewHolder(@NonNull View itemView){
            super(itemView);

            age = itemView.findViewById(R.id.agetext);
            name =  itemView.findViewById(R.id.nametext);
            weight = itemView.findViewById(R.id.weighttext);
            img = itemView.findViewById(R.id.img1);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

        }
    }
}
