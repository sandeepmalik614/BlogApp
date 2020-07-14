package com.blog.app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blog.app.R;
import com.blog.app.model.User;
import com.blog.app.utils.AppConstant;
import com.blog.app.utils.AppUtils;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.blog.app.utils.AppPrefrences.setFirebaseUserID;
import static com.blog.app.utils.AppPrefrences.setUserLoggedOut;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText edtOtp;
    private Button btVerify;
    private TextView tvResend;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode, name, mobile, type;
    private FirebaseAuth auth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        edtOtp = findViewById(R.id.editText6);
        btVerify = findViewById(R.id.button3);
        tvResend = findViewById(R.id.textView7);

        type = getIntent().getStringExtra("otpType");
        if (type.equals("registration")) {
            name = getIntent().getStringExtra("name");
        }
        mobile = getIntent().getStringExtra("mobile");

        if (AppUtils.isInternetAvailable(this)) {
            pd = new ProgressDialog(this);
            pd.setTitle("Please wait...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
            sendOtp();
            varifyMobile();
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtils.isInternetAvailable(OtpVerificationActivity.this)) {
                    pd.show();
                    String otp = edtOtp.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                    SigninWithPhone(credential);
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtils.isInternetAvailable(OtpVerificationActivity.this)) {
                    pd.show();
                    sendOtp();
                    varifyMobile();
                }else{
                    Toast.makeText(OtpVerificationActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendOtp() {
        auth = FirebaseAuth.getInstance();
        pd.dismiss();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(OtpVerificationActivity.this, "OTP Sent failed, Please click on Resend OTP.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(OtpVerificationActivity.this, "OTP has been sent on " + mobile, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void varifyMobile() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,                     // Phone number to verify
                2,                           // Timeout duration
                TimeUnit.MINUTES,                // Unit of timeout
                OtpVerificationActivity.this,        // Activity (for callback binding)
                mCallback);
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (type.equals("login")) {
                                loginUser();
                            } else {
                                registerUser();
                            }
                        } else {
                            pd.dismiss();
                            Toast.makeText(OtpVerificationActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loginUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(AppConstant.USER_TABLE)
                .child(auth.getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                try {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        setUserLoggedOut(OtpVerificationActivity.this, false);
                        setFirebaseUserID(OtpVerificationActivity.this, user.getFirebaseId());
                        Intent intent = new Intent(OtpVerificationActivity.this, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, "This mobile no is not registered with us, Please register.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(OtpVerificationActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(OtpVerificationActivity.this, "Error: " +
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(AppConstant.USER_TABLE).child(auth.getCurrentUser().getUid());
        HashMap<String, String> registerHash = new HashMap<>();
        registerHash.put("firebaseId", auth.getCurrentUser().getUid());
        registerHash.put("name", name);
        registerHash.put("mobile", mobile);
        registerHash.put("userImage", "");
        registerHash.put("creationDate", String.valueOf(System.currentTimeMillis()));
        reference.setValue(registerHash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                if (task.isSuccessful()) {
                    AppUtils.addMobileNumber(mobile);
                    setUserLoggedOut(OtpVerificationActivity.this, false);
                    setFirebaseUserID(OtpVerificationActivity.this, auth.getCurrentUser().getUid());
                    Intent intent = new Intent(OtpVerificationActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Exception: " +
                            task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}