package com.blog.app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blog.app.R;
import com.blog.app.adapter.GalleryAdapter;
import com.blog.app.model.GalleryData;
import com.blog.app.model.PostData;
import com.blog.app.utils.AppConstant;
import com.blog.app.utils.AppPrefrences;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static com.blog.app.utils.AppConstant.POST_IMAGE_TABLE;

public class FinalPostActivity extends AppCompatActivity {

    private EditText edtDisc;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private DatabaseReference reference;
    private ArrayList<GalleryData> galleryData;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_post);
        galleryData = new ArrayList<>();
        galleryData = (ArrayList<GalleryData>) getIntent().getSerializableExtra("images");

        edtDisc = findViewById(R.id.edittext5);
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new GalleryAdapter(this, galleryData, null));
        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        reference = FirebaseDatabase.getInstance().getReference();
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.setMessage("Please wait...");
    }

    public void createPost(View view) {
        if (edtDisc.getText().toString().isEmpty()) {

        } else {
            pd.show();
            ArrayList<String> imageList = new ArrayList<>();
            for (int i = 0; i < galleryData.size(); i++) {
                uploadImage(BitmapFactory.decodeFile(galleryData.get(i).getImage()), imageList, i);
            }
        }
    }

    private void finalSubmit(ArrayList<String> imageList) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(AppConstant.POST_TABLE)
                .child(AppPrefrences.getFirebaseUserID(this)).push();
        PostData postData = new PostData();
        postData.setImageUrls(imageList);
        postData.setPostCreationDate(String.valueOf(System.currentTimeMillis()));
        postData.setUserId(AppPrefrences.getFirebaseUserID(this));
        postData.setPostDescription(edtDisc.getText().toString());
        reference.setValue(postData);
        Toast.makeText(this, "Post created successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    private void uploadImage(Bitmap bitmap, final ArrayList<String> imageList, final int i) {
        StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();
        final StorageReference ref = mImageStorage.child(POST_IMAGE_TABLE)
                .child(AppPrefrences.getFirebaseUserID(this))
                .child(String.valueOf(System.currentTimeMillis() + ".jpg"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downUri = task.getResult();
                            imageList.add(downUri.toString());
                            if(i == (galleryData.size()-1)){
                                pd.dismiss();
                                finalSubmit(imageList);
                            }
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FinalPostActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}