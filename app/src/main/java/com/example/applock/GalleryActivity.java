package com.example.applock;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<String> imagePaths;
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = findViewById(R.id.gridView);
        imagePaths = new ArrayList<>();

        loadImages();

        adapter = new ImageAdapter(this, imagePaths);
        gridView.setAdapter(adapter);

        if (imagePaths.isEmpty()) {
            Toast.makeText(this, "No intruder images found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImages() {

        imagePaths.clear(); // prevent duplicates

        // 🔥 FIXED: use internal storage
        File folder = getFilesDir();

        if (folder != null && folder.exists()) {

            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {

                    if (file.getName().startsWith("intruder_")
                            && file.getName().endsWith(".jpg")) {

                        imagePaths.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadImages();
        adapter.notifyDataSetChanged();
    }
}