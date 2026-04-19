package com.example.applock;

import android.graphics.Bitmap;

public class FaceUtils {

    // 🔥 No ML Kit → no face detection
    // Just return original bitmap

    public interface FaceResult {
        void onResult(Bitmap bitmap);
    }

    public static void getFace(Bitmap bitmap, FaceResult callback) {
        // No detection → just pass full image
        callback.onResult(bitmap);
    }

    // 🔥 Dummy crop (returns full image)
    public static Bitmap cropFace(Bitmap bitmap) {
        return bitmap;
    }

    // 🔥 Simple image comparison (your old logic)
    public static float compareFaces(Bitmap b1, Bitmap b2) {

        Bitmap r1 = Bitmap.createScaledBitmap(b1, 100, 100, true);
        Bitmap r2 = Bitmap.createScaledBitmap(b2, 100, 100, true);

        long diff = 0;

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {

                int p1 = r1.getPixel(x, y);
                int p2 = r2.getPixel(x, y);

                int r = Math.abs((p1 >> 16 & 0xff) - (p2 >> 16 & 0xff));
                int g = Math.abs((p1 >> 8 & 0xff) - (p2 >> 8 & 0xff));
                int b = Math.abs((p1 & 0xff) - (p2 & 0xff));

                diff += r + g + b;
            }
        }

        return diff / (float)(100 * 100 * 3);
    }
}