package com.example.applock;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ImageView imageView = findViewById(R.id.fullImage);

        String path = getIntent().getStringExtra("IMAGE_PATH");

        if (path != null) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        }
    }
}