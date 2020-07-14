package com.blog.app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blog.app.R;
import com.blog.app.utils.AppUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.blog.app.utils.AppConstant.USER_PHONE;

public class RegistrationActivity extends AppCompatActivity {

    private EditText edtName, edtMobile;
    private Button btRegister;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        edtName = findViewById(R.id.editText2);
        edtMobile = findViewById(R.id.editText3);
        btRegister = findViewById(R.id.button2);

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait...");

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFields();
            }
        });
    }

    private void validateFields() {
        if (edtName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        } else if (edtMobile.getText().toString().isEmpty() &&
                edtMobile.getText().toString().length() < 10) {
            Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
        } else if(!AppUtils.isInternetAvailable(this)){
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }else{
            checkMobileNumber();
        }
    }

    public void goToLogin(View view) {
        onBackPressed();
    }

    private void checkMobileNumber(){
        pd.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(USER_PHONE);

        reference.child(edtMobile.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pd.dismiss();

                        if(snapshot.child("mobile").getValue() == null){
                            Intent intent = new Intent(RegistrationActivity.this, OtpVerificationActivity.class);
                            intent.putExtra("otpType", "registration");
                            intent.putExtra("name", edtName.getText().toString());
                            intent.putExtra("mobile", edtMobile.getText().toString());
                            startActivity(intent);
                        }else{
                            Toast.makeText(RegistrationActivity.this, "This mobile no is already registered with us, Please login", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RegistrationActivity.this, "Cancelled: "+
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}