package com.blog.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.blog.app.R;
import com.blog.app.adapter.ImageSliderAdapter;
import com.blog.app.model.PostData;

import java.util.ArrayList;

public class FullImageSliderActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image_slider);

        viewPager = findViewById(R.id.viewPager1);
        ArrayList<String> imageList = new ArrayList<>();
        imageList = (ArrayList<String>) getIntent().getSerializableExtra("data");
        ImageSliderAdapter adapter = new ImageSliderAdapter(this, imageList, null);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getIntent().getIntExtra("position", 0));
    }
}