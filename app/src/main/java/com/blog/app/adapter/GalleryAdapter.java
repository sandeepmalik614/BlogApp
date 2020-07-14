package com.blog.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blog.app.R;
import com.blog.app.listner.GalleryImageSelectedListener;
import com.blog.app.model.GalleryData;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<GalleryData> imageList;
    private GalleryImageSelectedListener listener;

    public GalleryAdapter(Context context, ArrayList<GalleryData> imageList, GalleryImageSelectedListener listener) {
        this.context = context;
        this.imageList = imageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        if(imageList.get(position).isSelected()){
            holder.imgSelected.setVisibility(View.VISIBLE);
        }else{
            holder.imgSelected.setVisibility(View.GONE);
        }

        Glide.with(context).load(imageList.get(position).getImage()).into(holder.imgBanner);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onImageClicked(position, imageList.get(position).isSelected());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgBanner, imgSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBanner = itemView.findViewById(R.id.imageView3);
            imgSelected = itemView.findViewById(R.id.imageView4);
        }
    }
}
