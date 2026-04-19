package com.example.applock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> images;

    public ImageAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        String path = images.get(position);

        // 🔥 FIX: Proper image loading
        imageView.setImageBitmap(BitmapFactory.decodeFile(path));

        // 🔥 CLICK → VIEW
        imageView.setOnClickListener(v -> {
            Intent i = new Intent(context, ImageViewActivity.class);
            i.putExtra("IMAGE_PATH", path);
            context.startActivity(i);
        });

        // 🔥 LONG PRESS → DELETE
        imageView.setOnLongClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Delete Image")
                    .setMessage("Delete this image?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        File file = new File(path);

                        if (file.exists()) {
                            file.delete();

                            images.remove(position);
                            notifyDataSetChanged();

                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });

        return imageView;
    }
}