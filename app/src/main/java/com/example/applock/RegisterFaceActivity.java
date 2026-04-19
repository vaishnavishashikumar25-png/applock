package com.example.applock;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;

public class RegisterFaceActivity extends AppCompatActivity {

    ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_face);

        // CAMERA PERMISSION
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);

        } else {
            startCamera();
        }

        // Capture immediately when screen opens
        findViewById(R.id.captureBtn).setOnClickListener(v -> capture());
    }

    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();

                imageCapture = new ImageCapture.Builder().build();

                provider.unbindAll();
                provider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_FRONT_CAMERA,
                        imageCapture
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void capture() {

        if (imageCapture == null) {
            Toast.makeText(this, "Camera not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(getFilesDir(), "owner.jpg");

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(file).build(),
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {

                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {

                        Bitmap bitmap =
                                BitmapFactory.decodeFile(file.getAbsolutePath());

                        // 🔥 NO ML KIT → direct use
                        Bitmap faceCrop = FaceUtils.cropFace(bitmap);

                        // overwrite owner image with processed version
                        try {
                            java.io.FileOutputStream fos =
                                    new java.io.FileOutputStream(file);
                            faceCrop.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(RegisterFaceActivity.this,
                                "Owner face saved",
                                Toast.LENGTH_SHORT).show();

                        finish();
                    }

                    @Override
                    public void onError(ImageCaptureException exception) {
                        exception.printStackTrace();
                        Toast.makeText(RegisterFaceActivity.this,
                                "Capture failed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}