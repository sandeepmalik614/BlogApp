package com.blog.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blog.app.R;
import com.blog.app.utils.AppUtils;

public class RegistrationActivity extends AppCompatActivity {

    private EditText edtName, edtMobile;
    private Button btRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        edtName = findViewById(R.id.editText2);
        edtMobile = findViewById(R.id.editText3);
        btRegister = findViewById(R.id.button2);

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
            registerUser(edtName.getText().toString(), edtMobile.getText().toString());
        }
    }

    private void registerUser(String name, String mobile) {
        Intent intent = new Intent(this, OtpVerificationActivity.class);
        intent.putExtra("otpType", "registration");
        intent.putExtra("name", name);
        intent.putExtra("mobile", mobile);
        startActivity(intent);
    }

    public void goToLogin(View view) {
        onBackPressed();
    }
}