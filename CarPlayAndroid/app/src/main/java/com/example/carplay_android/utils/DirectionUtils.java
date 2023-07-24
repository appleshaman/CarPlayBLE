package com.example.carplay_android.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.carplay_android.javabeans.JavaBeanBitmap;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DirectionUtils {
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
            javaBeanBitmap.setBitmapName(sample.split("\\.")[0]);
            String path = "direction_samples" + File.separator + sample;
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
        while (i2 < 14400) {
            if (ints1[i2] != ints2[i2]) {
                i1++;
            }
            i2++;
        }
        return (float) i1 / (float) 14400 * (float) 100;
    }

    public static String getDirectionByComparing(@NotNull Bitmap bitmapBeCompared) {
        int result = -1;
        int index = -1;
        int closest = 999;//
        int closestIndex = -1;
        for (int i = 0; i < bitmaps.size(); i++) {// compare each

            Bitmap bitmapComparedWith;
            bitmapBeCompared = Bitmap.createScaledBitmap(bitmapBeCompared, 120, 120, false);
            bitmapComparedWith = Bitmap.createScaledBitmap(bitmaps.get(i).getSampleBitmap(), 120, 120, false);
            float resultTemp = compareBitmaps(bitmapComparedWith, bitmapBeCompared);

            if((int)result < closest){
                closest = (int)result;
                closestIndex = i;
            }

            if (result == -1 || ((Float.compare(resultTemp, ((float) result)) < 0))) {
                result = (int) resultTemp;
                index = i;
            }
        }
        if (index != -1) {
            if (result <= 35) {
                return bitmaps.get(index).getBitmapName();
            }
            return bitmaps.get(closestIndex).getBitmapName();
        }
        return "UNKNOWN";
    }

    public static int getDirectionNumber(String direction) {
        int directionNumber = 34;
        switch (direction) {
            case "ARRIVE":
                directionNumber = 0;
                break;
            case "ARRIVE_LEFT":
                directionNumber = 1;
                break;
            case "ARRIVE_RIGHT":
                directionNumber = 2;
                break;
            case "CONTINUE_LEFT":
                directionNumber = 3;
                break;
            case "CONTINUE_RETURN":
                directionNumber = 4;
                break;
            case "CONTINUE_RIGHT":
                directionNumber = 5;
                break;
            case "CONTINUE_SLIGHT_LEFT":
                directionNumber = 6;
                break;
            case "CONTINUE_SLIGHT_RIGHT":
                directionNumber = 7;
                break;
            case "CONTINUE_STRAIGHT":
                directionNumber = 8;
                break;
            case "DEPART":
                directionNumber = 9;
                break;
            case "FORK":
                directionNumber = 10;
                break;
            case "POINTER":
                directionNumber = 11;
                break;
            case "ROTATORY_EXIT":
                directionNumber = 12;
                break;
            case "ROTATORY_EXIT_INVERTED":
                directionNumber = 13;
                break;
            case "ROTATORY_LEFT":
                directionNumber = 14;
                break;
            case "ROTATORY_LEFT_INVERTED":
                directionNumber = 15;
                break;
            case "ROTATORY_RIGHT":
                directionNumber = 16;
                break;
            case "ROTATORY_RIGHT_INVERTED":
                directionNumber = 17;
                break;
            case "ROTATORY_SHARP_LEFT":
                directionNumber = 18;
                break;
            case "ROTATORY_SHARP_LEFT_INVERTED":
                directionNumber = 19;
                break;
            case "ROTATORY_SHARP_RIGHT":
                directionNumber = 20;
                break;
            case "ROTATORY_SHARP_RIGHT_INVERTED":
                directionNumber = 21;
                break;
            case "ROTATORY_SLIGHT_LEFT":
                directionNumber = 22;
                break;
            case "ROTATORY_SLIGHT_LEFT_INVERTED":
                directionNumber = 23;
                break;
            case "ROTATORY_SLIGHT_RIGHT":
                directionNumber = 24;
                break;
            case "ROTATORY_SLIGHT_RIGHT_INVERTED":
                directionNumber = 25;
                break;
            case "ROTATORY_STRAIGHT":
                directionNumber = 26;
                break;
            case "ROTATORY_STRAIGHT_INVERTED":
                directionNumber = 27;
                break;
            case "ROTATORY_TOTAL":
                directionNumber = 28;
                break;
            case "ROTATORY_TOTAL_INVERTED":
                directionNumber = 29;
                break;
            case "SHARP_LEFT":
                directionNumber = 30;
                break;
            case "SHARP_RIGHT":
                directionNumber = 31;
                break;
            case "SLIGHT_LEFT":
                directionNumber = 32;
                break;
            case "SLIGHT_RIGHT":
                directionNumber = 33;
                break;
            case "UNKNOWN":
                directionNumber = 34;
                break;
        }
        return directionNumber;
    }
}
