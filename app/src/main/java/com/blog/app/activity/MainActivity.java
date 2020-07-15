package com.blog.app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blog.app.R;
import com.blog.app.fragment.HomeFragment;
import com.blog.app.fragment.ProfileFragment;
import com.blog.app.utils.AppPrefrences;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private boolean isHomeLoaded = true, isProfileLoaded = false, isDoubleBackClicked = false;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imgLogout = findViewById(R.id.imageView2);

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Logout");
                builder.setTitle("Do you want to logout?");
                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        AppPrefrences.clearAllPreferences(MainActivity.this);
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setOnNavigationItemSelectedListener(this);
        loadFragment(new HomeFragment());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                if (!isHomeLoaded) {
                    isHomeLoaded = true;
                    isProfileLoaded = false;
                    loadFragment(new HomeFragment());
                }
                break;

            case R.id.navigation_post:
                Intent intent = new Intent(MainActivity.this, CustomGalleryActivity.class);
                startActivity(intent);
                break;

            case R.id.navigation_profile:
                if (!isProfileLoaded) {
                    isProfileLoaded = true;
                    isHomeLoaded = false;
                    loadFragment(new ProfileFragment());
                }
                break;
        }

        return true;
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (isProfileLoaded) {
            isHomeLoaded = true;
            isProfileLoaded = false;
            navigationView.setSelectedItemId(R.id.navigation_home);
            loadFragment(new HomeFragment());
        } else if (isDoubleBackClicked) {
            super.onBackPressed();
        } else {
            isDoubleBackClicked = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isDoubleBackClicked = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onResume() {
        if(isHomeLoaded){
            navigationView.setSelectedItemId(R.id.navigation_home);
        }else {
            navigationView.setSelectedItemId(R.id.navigation_profile);
        }
        super.onResume();
    }
}