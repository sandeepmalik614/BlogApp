package com.blog.app.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.provider.Settings;
import android.telephony.AccessNetworkConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blog.app.R;
import com.blog.app.activity.LoginActivity;
import com.blog.app.model.User;
import com.blog.app.utils.AppConstant;
import com.blog.app.utils.AppPrefrences;
import com.blog.app.utils.AppUtils;
import com.blog.app.utils.RealPathUtil;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.blog.app.utils.AppConstant.USER_IMAGE_TABLE;
import static com.blog.app.utils.AppConstant.USER_TABLE;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvMobile;
    private CircleImageView imgUser;
    private ImageView imgEdit;
    private User userData;
    private DatabaseReference reference;
    private ProgressDialog pd;
    private static final int IMAGE_REQUEST = 1;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        tvName = view.findViewById(R.id.textView8);
        tvMobile = view.findViewById(R.id.textView9);
        imgUser = view.findViewById(R.id.circleImageView);
        imgEdit = view.findViewById(R.id.imageView1);
        reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        getUserProfile();

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkExternalStoragePermission()) {
                    openGalleryIntent();
                } else {
                    requestExternalStoragePermission();
                }
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userData.getUserImage().isEmpty()) {
                    AppUtils.seeFullImage(getActivity(), userData.getUserImage(), imgUser);
                }
            }
        });
    }

    private void openGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), IMAGE_REQUEST);
    }

    private void getUserProfile() {
        reference.child(AppConstant.USER_TABLE).child(AppPrefrences.getFirebaseUserID(getActivity()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pd.dismiss();
                        try {
                            userData = snapshot.getValue(User.class);
                            tvName.setText(userData.getName());
                            tvMobile.setText(userData.getMobile());
                            Glide.with(getActivity()).load(userData.getUserImage()).into(imgUser);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Exception: " +
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Cancelled: " +
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            openUtilityDialog(getActivity(), "External Storage permission is required. Please allow this permission in App Settings.");
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1011);
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
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGalleryIntent();
        } else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            openUtilityDialog(getActivity(), "You Have To Give Permission From Your Device Setting To go in Setting Please Click on Settings Button");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

            try {
                uploadImageToServer(AppUtils.rotateImageIfRequired(bitmap, RealPathUtil.getPath(getActivity(), selectedImage)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToServer(Bitmap bitmap) {
        StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();
        final StorageReference ref = mImageStorage.child(USER_IMAGE_TABLE)
                .child(AppPrefrences.getFirebaseUserID(getActivity()) + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] data = baos.toByteArray();
        pd.show();

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
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("userImage", downUri.toString());
                            reference.child(USER_TABLE).child(AppPrefrences.getFirebaseUserID(getActivity()))
                                    .updateChildren(hashMap);
                            Toast.makeText(getActivity(), "Image updated successfully", Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}