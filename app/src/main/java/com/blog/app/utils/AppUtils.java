package com.blog.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.blog.app.activity.FullImageViewActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.blog.app.utils.AppConstant.USER_PHONE;

public class AppUtils {

    public static boolean isInternetAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Bitmap rotateImageIfRequired(Bitmap bitmap, String selectedImage) throws IOException {
        ExifInterface ei = new ExifInterface(selectedImage);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @SuppressLint("NewApi")
    public static void seeFullImage(Context context, String imageUrl, ImageView imageView) {
        Intent intent = new Intent(context, FullImageViewActivity.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(imageView, "fullImage");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
        intent.putExtra("fullImage", imageUrl);
        context.startActivity(intent, options.toBundle());
    }

    public static void addMobileNumber(String phone) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(USER_PHONE).child(phone);
        HashMap<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        reference.setValue(map);
    }

    @SuppressLint("NewApi")
    public static String getTimeAgo(long millis) {

        long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - millis);
        long hours = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - millis);
        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - millis);

        if (seconds < 60) {
            return String.valueOf(seconds) + " seconds ago";
        } else if (minutes == 1) {
            return String.valueOf(minutes) + " minute ago";
        } else if (minutes < 60) {
            return String.valueOf(minutes) + " minutes ago";
        } else if (hours == 1) {
            return String.valueOf(hours) + " hour ago";
        } else if (hours < 24) {
            return String.valueOf(hours) + " hours ago";
        } else if (days == 1) {
            return String.valueOf(days) + " day ago";
        } else if (days < 30) {
            return String.valueOf(days) + " days ago";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aaa");
            return formatter.format(new Date(millis));
        }
    }
}
