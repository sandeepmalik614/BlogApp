package com.blog.app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.blog.app.R;
import com.blog.app.adapter.GalleryAdapter;
import com.blog.app.listner.GalleryImageSelectedListener;
import com.blog.app.model.GalleryData;
import com.blog.app.model.ImagesGallery;

import java.util.ArrayList;

public class CustomGalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<GalleryData> galleryData;
    private ArrayList<Integer> selectedPositions = new ArrayList<>();
    private GalleryAdapter adapter;
    private static final int MY_READ_CODE = 101;
    private int count = 0;

    private GalleryImageSelectedListener listener = new GalleryImageSelectedListener() {
        @Override
        public void onImageClicked(int pos, boolean isSelected) {
            if (isSelected) {
                for (int i = 0; i < selectedPositions.size(); i++) {
                    if (selectedPositions.get(i) == pos) {
                        selectedPositions.remove(i);
                    }
                }
                count--;
                galleryData.get(pos).setSelected(false);
                adapter.notifyDataSetChanged();
            } else {
                if (count == 5) {
                    Toast.makeText(CustomGalleryActivity.this, "You can select only 5 images", Toast.LENGTH_SHORT).show();
                } else {
                    selectedPositions.add(pos);
                    count++;
                    galleryData.get(pos).setSelected(true);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_gallery);

        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        if (ContextCompat.checkSelfPermission(CustomGalleryActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_CODE);
        } else {
            loadImages();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadImages();
        } else {
            openUtilityDialog(this, "You Have To Give Permission From Your Device Setting To go in Setting Please Click on Settings Button");
        }
    }

    private void openUtilityDialog(final Context ctx, final String messageID) {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ctx);
        dialog.setMessage(messageID);
        dialog.setCancelable(false);
        dialog.setPositiveButton("GO TO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void loadImages() {
        galleryData = new ArrayList<>();
        galleryData.addAll(ImagesGallery.listOfImage(this));
        adapter = new GalleryAdapter(this, galleryData, listener);
        recyclerView.setAdapter(adapter);
    }

    public void closeGallery(View view) {
        finish();
    }

    public void postFirstNext(View view) {
        if (count == 0) {
            Toast.makeText(this, "Please select atleast 1 pic", Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<GalleryData> list = new ArrayList<>();
            for (int i = 0; i < selectedPositions.size(); i++) {
                list.add(galleryData.get(selectedPositions.get(i)));
                list.get(i).setSelected(false);
            }

            Intent intent = new Intent(this, FinalPostActivity.class);
            intent.putExtra("images", list);
            startActivity(intent);
        }
    }

}