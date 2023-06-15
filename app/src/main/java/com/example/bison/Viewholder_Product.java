package com.example.bison;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Viewholder_Product extends RecyclerView.ViewHolder {

    ImageView productView;
    CircleImageView imageView;
    TextView product_result, contact_result, price_result, time_result;
    public View v;

    public Viewholder_Product(@NonNull View itemView) {

        super(itemView);
        v = itemView;
    }

    public void setitem(FragmentActivity activity ,String name, String url, String userid, String key, String privacy, String time,
    String product, String productImgUrl, String location, String contact, String price, String description)
    {
        productView = itemView.findViewById(R.id.iv_product_item);
        imageView = itemView.findViewById(R.id.iv_profile_item);

        product_result = itemView.findViewById(R.id.tv_product_item);
        price_result = itemView.findViewById(R.id.tv_price_item);
        time_result = itemView.findViewById(R.id.tv_time_item);

        Picasso.get().load(url).into(imageView);
        Picasso.get().load(productImgUrl).into(productView);

        product_result.setText(product);
        price_result.setText(price);
        time_result.setText(time);

    }

}
