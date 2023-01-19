package com.example.carplay_android.javabeans;

import android.graphics.Bitmap;

import java.io.Serializable;

public class JavaBeanBitmap implements Serializable {
    private String bitmapName;
    private Bitmap sampleBitmap;


    public void setBitmapName(String bitmapName) {
        this.bitmapName = bitmapName;
    }

    public void setSampleBitmap(Bitmap sampleBitmap) {
        this.sampleBitmap = sampleBitmap;
    }

    public Bitmap getSampleBitmap() {
        return sampleBitmap;
    }

    public String getBitmapName() {
        return bitmapName;
    }


}
