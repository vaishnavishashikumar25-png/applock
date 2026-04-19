package com.example.applock;

import android.graphics.Bitmap;

public class FaceRecognizer {

    // 🔥 Dummy embedding (just resize image)
    public float[] getEmbedding(Bitmap bitmap) {

        Bitmap resized =
                Bitmap.createScaledBitmap(bitmap, 50, 50, true);

        float[] embedding = new float[50 * 50];

        int index = 0;

        for (int y = 0; y < 50; y++) {
            for (int x = 0; x < 50; x++) {

                int pixel = resized.getPixel(x, y);

                int gray = ((pixel >> 16 & 0xff)
                        + (pixel >> 8 & 0xff)
                        + (pixel & 0xff)) / 3;

                embedding[index++] = gray / 255f;
            }
        }

        return embedding;
    }

    // 🔥 Compare two embeddings
    public float compare(float[] emb1, float[] emb2) {

        float sum = 0;

        for (int i = 0; i < emb1.length; i++) {
            float diff = emb1[i] - emb2[i];
            sum += diff * diff;
        }

        return (float) Math.sqrt(sum);
    }
}