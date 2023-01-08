package com.example.carplay_android;

import android.graphics.Bitmap;

import java.io.Serializable;

public class JavaBeanBitmap implements Serializable {
    private String bitmapName;
    private String bitmapDir;
    private Bitmap sampleBitmap;

    public void setBitmapDir(String bitmapDir) {
        this.bitmapDir = bitmapDir;
    }

    public void setBitmapName(String bitmapName) {
        this.bitmapName = bitmapName;
    }

    public void setSampleBitmap(Bitmap sampleBitmap) {
        this.sampleBitmap = sampleBitmap;
    }

    public Bitmap getSampleBitmap() {
        return sampleBitmap;
    }

    public String getBitmapDir() {
        return bitmapDir;
    }

    public String getBitmapName() {
        return bitmapName;
    }
}
