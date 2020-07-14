package com.blog.app.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.SyncStateContract;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;

public class ImagesGallery {

    public static ArrayList<GalleryData> listOfImage(Context context) {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        ArrayList<GalleryData> imageList = new ArrayList<>();
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String projection[] = {MediaStore.MediaColumns.DATA,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection, null,
                null, orderBy+" DESC");
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()){
            imageList.add(new GalleryData(String.valueOf(cursor.getString(column_index_data)), false));
        }
        return imageList;
    }

}
