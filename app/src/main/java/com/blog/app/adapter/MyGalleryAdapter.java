package com.blog.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blog.app.R;
import com.blog.app.activity.FullImageSliderActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyGalleryAdapter extends RecyclerView.Adapter<MyGalleryAdapter.ViewHodler> {

    private Context context;
    private ArrayList<String> images;

    public MyGalleryAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_images, parent, false);
        return new ViewHodler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodler holder, final int position) {
        Glide.with(context).load(images.get(position)).into(holder.imgBanner);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullImageSliderActivity.class);
                intent.putExtra("data", images);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHodler extends RecyclerView.ViewHolder {

        private ImageView imgBanner;

        public ViewHodler(@NonNull View itemView) {
            super(itemView);

            imgBanner = itemView.findViewById(R.id.imageView6);
        }
    }
}
