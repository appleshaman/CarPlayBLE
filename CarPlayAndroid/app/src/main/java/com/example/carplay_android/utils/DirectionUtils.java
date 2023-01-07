package com.example.carplay_android.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.example.carplay_android.JavaBeanBitmap;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DirectionUtils {
    private static ArrayList<Bitmap> bitmapSamples;
    private static ArrayList<JavaBeanBitmap> bitmaps = new ArrayList<JavaBeanBitmap>();

    public static void loadSamplesFromAsserts(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] samples = new String[0];
        try {
            samples = assetManager.list("direction_samples");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String sample : samples) {
            JavaBeanBitmap javaBeanBitmap = new JavaBeanBitmap();
            javaBeanBitmap.setBitmapName(sample);
            String path = "direction_samples" + File.separator + sample;
            javaBeanBitmap.setBitmapDir(path);
            try {
                InputStream inputStream = context.getAssets().open(path);
                javaBeanBitmap.setSampleBitmap(BitmapFactory.decodeStream(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmaps.add(javaBeanBitmap);
        }
    }

    private static float compareBitmaps(Bitmap bitmap1, Bitmap bitmap2) {
        int[] ints1 = new int[14400];
        int[] ints2 = new int[14400];
        bitmap1.getPixels(ints1, 0, 120, 0, 0, 120, 120);
        bitmap2.getPixels(ints2, 0, 120, 0, 0, 120, 120);
        int i1 = 0;
        int i2 = 0;
        while (i2 < 14399) {
            if (ints1[i2] != ints2[i2]) {
                i1++;
            }
            i2++;
        }
        return (float) i1 / (float) 14400 * (float) 100;
    }

    public static String getDirectionByComparing(@NotNull Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
        int result = -1;
        int index = -1;
        for (int i = 0; i < bitmaps.size(); i++) {
            float resultTemp = compareBitmaps(bitmaps.get(i).getSampleBitmap(), bitmap);
            if (result == -1 || ((Float.compare(resultTemp, ((float) result)) < 0))) {
                result = (int) resultTemp;
                index = i;
            }
        }
        if (index == -1) {
            if (result <= 35) {
                return bitmaps.get(index).getBitmapName();
            }
        }
        return "UNKNOWN";
    }
}
