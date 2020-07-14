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
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText edtMobile;
    private Button btLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        edtMobile = findViewById(R.id.editText);
        btLogin = findViewById(R.id.button);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFields();
            }
        });
    }

    private void validateFields() {
        if(edtMobile.getText().toString().isEmpty() &&
            edtMobile.getText().toString().length() < 10){
            Toast.makeText(this, "Please enter a valid 10 digit mobile", Toast.LENGTH_SHORT).show();
        }else if(!AppUtils.isInternetAvailable(this)){
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }else {
            loginUser();
        }
    }

    private void loginUser() {
        Intent intent = new Intent(this, OtpVerificationActivity.class);
        intent.putExtra("otpType", "login");
        intent.putExtra("mobile", edtMobile.getText().toString());
        startActivity(intent);
    }

    public void goToRegister(View view) {
        startActivity(new Intent(this, RegistrationActivity.class));
    }
}